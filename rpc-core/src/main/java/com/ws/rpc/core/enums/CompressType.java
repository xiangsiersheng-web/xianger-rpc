package com.ws.rpc.core.enums;

/**
 * 
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:08
 */
public enum CompressType {
    NONE((byte) 0);

    private final byte type;
    CompressType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public static CompressType valueOf(byte type) {
        for (CompressType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown serialization type : " + type);
    }

    public static CompressType fromString(String name) {
        for (CompressType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown serialization type : " + name);
    }
}
