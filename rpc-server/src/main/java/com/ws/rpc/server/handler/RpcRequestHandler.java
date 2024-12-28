package com.ws.rpc.server.handler;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.server.store.LocalServiceCache;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 4:14
 */

@Slf4j
public class RpcRequestHandler {
    public Object handleRpcRequest(RpcRequest request) throws Exception {
        Object service = LocalServiceCache.getService(request.getServiceKey());
        if (service == null) {
            log.error("Service not found: {}", request.getServiceKey());
            throw new RpcException("Service not found: " + request.getServiceKey());
        }
        Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(service, request.getParameters());
    }
}
