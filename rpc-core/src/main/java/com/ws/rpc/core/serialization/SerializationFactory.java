package com.ws.rpc.core.serialization;

import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.jdk.JdkSerialization;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:09
 */
public class SerializationFactory {
    public static Serialization getSerialization(SerializationType serializationType) {
        switch (serializationType) {
            case JDK:
                return new JdkSerialization();
            default:
                throw new RuntimeException("Unsupported serialization type: " + serializationType);
        }
    }
}
