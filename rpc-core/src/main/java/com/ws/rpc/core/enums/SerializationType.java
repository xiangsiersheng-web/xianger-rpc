package com.ws.rpc.core.enums;

import lombok.Getter;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:06
 */
@Getter
public enum SerializationType {
    JDK((byte) 1),
    JSON((byte) 2),
    HESSIAN((byte) 3),
    KRYO((byte) 4),
    PROTOSTUFF((byte) 5);

    private final byte type;
    SerializationType(byte type) {
        this.type = type;
    }

    public static SerializationType valueOf(byte type) {
        for (SerializationType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown serialization type : " + type);
    }

    /**
     * 根据字符串名称获取枚举实例（忽略大小写）
     *
     * @param name 枚举名称
     * @return SerializationType 枚举实例
     * @throws IllegalArgumentException 如果找不到匹配的枚举
     */
    public static SerializationType fromString(String name) {
        for (SerializationType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown serialization type: " + name);
    }
}
