package com.ws.rpc.client.transport.socket;

import com.ws.rpc.client.dto.RequestMetaData;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.core.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 1:04
 */

@Slf4j
public class SocketRpcClient implements RpcClient {
    @Override
    public RpcResponse sendRequest(RequestMetaData requestMetaData) {
        return null;
    }
}
