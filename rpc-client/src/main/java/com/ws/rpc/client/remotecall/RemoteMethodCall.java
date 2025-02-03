package com.ws.rpc.client.remotecall;

import com.ws.rpc.client.config.CircuitBreakerProperties;
import com.ws.rpc.client.config.RetryProperties;
import com.ws.rpc.client.config.RpcClientProperties;
import com.ws.rpc.client.dto.RpcRequestMetaData;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.core.enums.RetryStrategyType;
import com.ws.rpc.core.fault.retry.RetryStrategy;
import com.ws.rpc.core.fault.retry.RetryStrategyFactory;
import com.ws.rpc.core.protection.circuit.CircuitBreaker;
import com.ws.rpc.core.protocol.ProtocolConstants;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.enums.MessageType;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.protocol.MessageHeader;
import com.ws.rpc.core.protocol.RpcMessage;
import com.ws.rpc.core.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:23
 */
@Slf4j
public class RemoteMethodCall {
    private final RpcClientProperties rpcClientProperties;
    private final RetryProperties retryProperties;
    private final CircuitBreakerProperties circuitBreakerProperties;
    private final RpcClient rpcClient;
    private final ServiceDiscovery serviceDiscovery;

    public RemoteMethodCall(RpcClientProperties rpcClientProperties,
                            RetryProperties retryProperties,
                            CircuitBreakerProperties circuitBreakerProperties,
                            RpcClient rpcClient,
                            ServiceDiscovery serviceDiscovery) {
        this.rpcClientProperties = rpcClientProperties;
        this.retryProperties = retryProperties;
        this.circuitBreakerProperties = circuitBreakerProperties;
        this.rpcClient = rpcClient;
        this.serviceDiscovery = serviceDiscovery;
    }

    public Object call(String serviceKey, Method method, Object[] args) {
        return call(serviceKey, method, args, 0, 0);
    }

    public Object call(String serviceKey, Method method, Object[] args, int timeout, int retry) {
        if (!circuitBreakerProperties.isEnabled()) {
            return doCall(serviceKey, method, args, timeout, retry);
        } else {
            // 获取熔断器
            CircuitBreaker breaker = CircuitBreakerManager.getBreaker(serviceKey + "#" + method.getName(), circuitBreakerProperties);

            // 熔断检查
            if (!breaker.allowRequest()) {
                log.warn("Circuit breaker OPEN for {}.{}", serviceKey, method.getName());
                throw new RpcException("Service unavailable due to circuit breaker");
            }

            try {
                // 实际调用
                Object result = doCall(serviceKey, method, args, timeout, retry);
                breaker.recordSuccess(); // 记录成功
                return result;
            } catch (Exception e) {
                breaker.recordFailure(); // 记录失败
                throw e;
            }
        }
    }

    private Object doCall(String serviceKey, Method method, Object[] args, int timeout, int retry) {
        // 构造request
        RpcRequest request = RpcRequest.builder()
                .serviceKey(serviceKey)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        // 根据request进行服务发现
        ServiceInfo serviceInfo = serviceDiscovery.discover(request);
        if (serviceInfo == null) {
            log.error("Service discovery failed for " + serviceKey);
            throw new RpcException("Service discovery failed for " + serviceKey);
        }
        // 构造消息头
        MessageHeader header = new MessageHeader();
        header.setMessageId(ProtocolConstants.MessageIdGenerator.nextId()); // 生成消息id
        header.setMessageType(MessageType.REQUEST.getCode());
        header.setCompressionAlgorithm(
                CompressionType.fromString(rpcClientProperties.getCompression()).getType());
        header.setSerializationAlgorithm(
                SerializationType.fromString(rpcClientProperties.getSerialization()).getType());

        // 构造请求消息
        RpcMessage rpcMessage = new RpcMessage(header, request);
        timeout = timeout <= 0 ? rpcClientProperties.getTimeout() : timeout;    // 请求超时
        retry = retry <= 0 ? retryProperties.getRetryMax() : retry;            // 重试次数
        RpcRequestMetaData requestMetaData = RpcRequestMetaData.builder()
                .rpcMessage(rpcMessage)
                .serviceAddress(serviceInfo.getHost())
                .servicePort(serviceInfo.getPort())
                .timeout(timeout)
                .build();

        // 获取重试策略
        RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy(
                RetryStrategyType.fromString(retryProperties.getRetryStrategy()));

        // 使用重试策略进行远程调用
        RpcResponse response;
        Callable<RpcResponse> callable = () -> rpcClient.sendRequest(requestMetaData);
        try {
            response = retryStrategy.executeWithRetry(callable, retry, retryProperties.getRetryInterval());
        } catch (Exception e) {
            log.error("Remote procedure call failure.");
            throw new RpcException("Remote procedure call failure. " + requestMetaData);
        }

        // 获取响应消息
        if (response.getException() != null) {
            throw new RpcException(response.getException());
        }
        return response.getResult();
    }

    @PreDestroy
    public void close() throws Exception {
        try {
            if (serviceDiscovery != null) {
                serviceDiscovery.destroy();
            }
            if (rpcClient != null) {
                rpcClient.close();
            }
            log.info("Rpc client resource release completed and exited successfully.");
        } catch (Exception e) {
            log.warn("An exception occurred while executing the destroy operation when the rpc client exited, {}.",
                    e.getMessage());
        }
    }
}
