package com.ws.rpc.core.dto;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:21
 */
public class RpcRequest {
    /**
     * 服务名+版本
     */
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
