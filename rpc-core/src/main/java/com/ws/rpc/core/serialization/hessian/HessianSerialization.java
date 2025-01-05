package com.ws.rpc.core.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 16:55
 */
public class HessianSerialization implements Serialization {
    @Override
    public <T> byte[] doSerialize(T obj) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Hessian serialization failed.", e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);) {
            Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);
            Object obj = hessian2Input.readObject();
            return clazz.cast(obj);
        } catch (IOException e) {
            throw new SerializationException("Hessian deserialization failed.", e);
        }
    }
}
