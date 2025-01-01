package com.ws.rpc.core.constants;

/**
 * 
 * @author ws
 * @version 1.0
 * @date 2025-01-01 19:55
 */
public class RpcConstants {
    public static final byte[] MAGIC_NUMBER = new byte[]{(byte) 'x', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;
    public static final byte HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 4 * 1024 * 1024;
}
