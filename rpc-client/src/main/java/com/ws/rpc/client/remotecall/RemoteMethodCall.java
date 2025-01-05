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
        RpcRequestMetaData requestMetaData = RpcRequestMetaData.builder()
                .rpcMessage(rpcMessage)
                .serviceAddress(serviceInfo.getAddress())
                .servicePort(serviceInfo.getPort())
                .timeout(rpcClientProperties.getTimeout())
                .build();
        // 调用rpcClient发送请求
        RpcResponse response = rpcClient.sendRequest(requestMetaData);
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
}
