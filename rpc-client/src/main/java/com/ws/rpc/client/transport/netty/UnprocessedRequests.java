package com.ws.rpc.client.transport.netty;

import com.ws.rpc.core.protocol.RpcMessage;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 21:22
 */
public class UnprocessedRequests {
    private static final Map<Integer, Promise<RpcMessage>> UNPROCESSED_REQUESTS = new ConcurrentHashMap<>();

    public static void put(Integer requestId, Promise<RpcMessage> promise) {
        UNPROCESSED_REQUESTS.put(requestId, promise);
    }

    public static Promise<RpcMessage> remove(Integer requestId) {
        return UNPROCESSED_REQUESTS.remove(requestId);
    }
}
