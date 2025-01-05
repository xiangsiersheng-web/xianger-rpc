package com.ws.rpc.core.serialization.protostuff;

import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 17:33
 */
public class ProtostuffSerialization implements Serialization {

    // 使用 ThreadLocal 来优化内存使用，避免多线程并发问题
    private static final ThreadLocal<LinkedBuffer> BUFFER_THREAD_LOCAL = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] doSerialize(T obj) throws Exception {
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = BUFFER_THREAD_LOCAL.get();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ProtostuffIOUtil.writeTo(byteArrayOutputStream, obj, schema, buffer);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Protostuff serialization failed.", e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) throws Exception {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            T obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(byteArrayInputStream, obj, schema);
            return obj;
        } catch (IOException e) {
            throw new SerializationException("Protostuff deserialization failed.", e);
        }
    }
}
