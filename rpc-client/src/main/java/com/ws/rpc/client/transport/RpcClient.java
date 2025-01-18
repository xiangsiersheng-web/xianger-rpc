package com.ws.rpc.client.transport;

import com.ws.rpc.client.dto.RpcRequestMetaData;

import com.ws.rpc.core.dto.RpcResponse;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:05
 */
public interface RpcClient {
    /**
     * 具体的实现类根据 requestMetaData 发送请求
     * @param rpcRequestMetaData
     * @return
     */
    RpcResponse sendRequest(RpcRequestMetaData rpcRequestMetaData);

    default void close() {}
}
