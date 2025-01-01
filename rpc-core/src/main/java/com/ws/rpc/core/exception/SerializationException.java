package com.ws.rpc.core.exception;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:18
 */
public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }

    public SerializationException() {
        super();
    }
}
