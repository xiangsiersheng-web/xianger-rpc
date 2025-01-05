package com.ws.rpc.core.serialization;

import com.ws.rpc.core.exception.SerializationException;

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
    default <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new SerializationException("Serialization object is null.");
        }
        try {
            return doSerialize(obj);
        } catch (Exception e) {
            throw new SerializationException("Serialization failed.", e);
        }
    }


    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            throw new SerializationException("Data to deserialize cannot be null or empty.");
        }
        if (clazz == null) {
            throw new SerializationException("Target class cannot be null.");
        }
        try {
            return doDeserialize(data, clazz);
        } catch (Exception e) {
            throw new SerializationException("Deserialization failed.", e);
        }
    }

    <T> byte[] doSerialize(T obj) throws Exception;

    <T> T doDeserialize(byte[] data, Class<T> clazz) throws Exception;
}
