package com.ws.rpc.core.serialization;

import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.serialization.hessian.HessianSerialization;
import com.ws.rpc.core.serialization.jdk.JdkSerialization;
import com.ws.rpc.core.serialization.json.FastJsonSerialization;
import com.ws.rpc.core.serialization.json.GsonSerialization;
import com.ws.rpc.core.serialization.kryo.KryoSerialization;
import com.ws.rpc.core.serialization.protostuff.ProtostuffSerialization;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:09
 */
public class SerializationFactory {
    public static Serialization getSerialization(SerializationType serializationType) {
        switch (serializationType) {
            case JDK:
                return SingletonFactory.getInstance(JdkSerialization.class);
            case JSON:
//                return SingletonFactory.getInstance(GsonSerialization.class);
                return SingletonFactory.getInstance(FastJsonSerialization.class);
            case HESSIAN:
                return SingletonFactory.getInstance(HessianSerialization.class);
            case KRYO:
                return SingletonFactory.getInstance(KryoSerialization.class);
            case PROTOSTUFF:
                return SingletonFactory.getInstance(ProtostuffSerialization.class);
            default:
                throw new RuntimeException("Unsupported serialization type: " + serializationType);
        }
    }
}
