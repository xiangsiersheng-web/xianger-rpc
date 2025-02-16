package com.ws.rpc.core.extension;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-16 13:23
 */
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
