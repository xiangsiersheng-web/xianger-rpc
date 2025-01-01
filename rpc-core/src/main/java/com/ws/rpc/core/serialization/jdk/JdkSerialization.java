package com.ws.rpc.core.serialization.jdk;

import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;

import java.io.*;

/**
 * jdk序列化，使用ObjectOutputStream和ObjectInputStream
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:12
 */
public class JdkSerialization implements Serialization {
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Jdk serialize failed.", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Jdk deserialize failed.", e);
        }
    }
}
