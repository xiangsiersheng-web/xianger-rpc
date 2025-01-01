package com.ws.rpc.core.serialization;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:10
 */
public interface Serialization {
    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);


    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
