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
    public <T> byte[] doSerialize(T obj) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);) {
            oos.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);) {
            Object obj = ois.readObject();
            return clazz.cast(obj);
        }
    }
}
