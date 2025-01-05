package com.ws.rpc.core.enums;

import lombok.Getter;

/**
 * 消息类型枚举，表示不同的RPC消息类型。
 * <ul>
 *     <li>REQUEST: 请求消息</li>
 *     <li>RESPONSE: 响应消息</li>
 *     <li>HEARTBEAT_REQUEST: 心跳请求</li>
 *     <li>HEARTBEAT_RESPONSE: 心跳响应</li>
 * </ul>
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:11
 */
@Getter
public enum MessageType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEARTBEAT_REQUEST((byte) 3),
    HEARTBEAT_RESPONSE((byte) 4);

    private final byte code;

    MessageType(byte code) {
        this.code = code;
    }

    public static MessageType valueOf(byte code) {
        for (MessageType messageType : values()) {
            if (messageType.code == code) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + code);
    }
}
