package com.ws.rpc.core.enums;

import lombok.Getter;

/**
 * 
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:08
 */
@Getter
public enum CompressionType {
    UN_COMPRESSION((byte) 0),
    GZIP((byte) 1);

    private final byte type;
    CompressionType(byte type) {
        this.type = type;
    }

    public static CompressionType valueOf(byte type) {
        for (CompressionType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown serialization type : " + type);
    }

    public static CompressionType fromString(String name) {
        for (CompressionType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown serialization type : " + name);
    }
}
