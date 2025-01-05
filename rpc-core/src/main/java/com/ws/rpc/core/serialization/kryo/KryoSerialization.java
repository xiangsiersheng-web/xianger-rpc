package com.ws.rpc.core.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 17:07
 */
public class KryoSerialization implements Serialization {

    // 每个线程都有一个自己的kryo，保证线程安全
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);   // 注册RpcResponse类（需要序列化的类之一）
        kryo.register(RpcRequest.class);    // 注册RpcRequest类（需要序列化的类之一）
        return kryo;
    });

    @Override
    public <T> byte[] doSerialize(T obj) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("Kryo serialization failed.", e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             Input input = new Input(byteArrayInputStream);) {
            Kryo kryo = kryoThreadLocal.get();
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            throw new SerializationException("Kryo deserialization failed.", e);
        }
    }
}
