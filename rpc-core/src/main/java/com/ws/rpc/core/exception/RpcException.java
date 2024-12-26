package com.ws.rpc.core.exception;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:34
 */
public class RpcException extends RuntimeException {
    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
