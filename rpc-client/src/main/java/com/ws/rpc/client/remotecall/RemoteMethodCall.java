package com.ws.rpc.client.remotecall;

import com.ws.rpc.client.config.RpcClientProperties;
import com.ws.rpc.client.dto.RpcRequestMetaData;
import com.ws.rpc.client.transport.RpcClient;
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

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:23
 */
@Slf4j
public class RemoteMethodCall {
    private final RpcClientProperties rpcClientProperties;
    private final RpcClient rpcClient;
    private final ServiceDiscovery serviceDiscovery;

    public RemoteMethodCall(RpcClientProperties rpcClientProperties, RpcClient rpcClient, ServiceDiscovery serviceDiscovery) {
        this.rpcClientProperties = rpcClientProperties;
        this.rpcClient = rpcClient;
        this.serviceDiscovery = serviceDiscovery;
    }

    public Object call(String serviceKey, Method method, Object[] args) {
        return call(serviceKey, method, args, 0, 0);
    }

    public Object call(String serviceKey, Method method, Object[] args, int timeout, int retry) {
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
        header.setMessageId(ProtocolConstants.MessageIdGenerator.nextId());
        header.setMessageType(MessageType.REQUEST.getCode());
        header.setCompressionAlgorithm(
                CompressionType.fromString(rpcClientProperties.getCompression()).getType());
        header.setSerializationAlgorithm(
                SerializationType.fromString(rpcClientProperties.getSerialization()).getType());

        // 构造请求消息
        RpcMessage rpcMessage = new RpcMessage(header, request);
        timeout = timeout <= 0 ? rpcClientProperties.getTimeout() : timeout;    // 超时
        retry = retry <= 0 ? rpcClientProperties.getRetry() : retry;            // 重试次数
        RpcRequestMetaData requestMetaData = RpcRequestMetaData.builder()
                .rpcMessage(rpcMessage)
                .serviceAddress(serviceInfo.getHost())
                .servicePort(serviceInfo.getPort())
                .timeout(timeout)
                .build();

        // 调用rpcClient发送请求
        RpcResponse response = null;
        int attempt = 0;
        while (response == null && attempt <= retry) {
            try {
                response = rpcClient.sendRequest(requestMetaData);
            } catch (Exception e) {
                if (attempt >= retry) {
                    log.warn("Rpc retry {} all failed", retry);
                    break;
                }
                attempt += 1;
                long sleepTime = (long) timeout * attempt;
                log.warn("Rpc remote call failed (attempt {} of {}). Retrying in {} ms.",
                        attempt, retry, sleepTime);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();  // 如果被中断，则恢复中断状态
                    log.error("Thread interrupted during retry sleep.", ex);
                    throw new RpcException("Rpc call interrupted during retries.", ex);
                }
            }
        }
        if (response == null) {
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
            log.info("Rpc client resource release completed and exited successfully.");
        } catch (Exception e) {
            log.warn("An exception occurred while executing the destroy operation when the rpc client exited, {}.",
                    e.getMessage());
        }
    }
}
