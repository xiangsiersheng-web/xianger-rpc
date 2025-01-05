package com.ws.rpc.core.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author ws
 * @version 1.0
 * @date 2025-01-01 19:55
 */
public class ProtocolConstants {
    public static final byte[] MAGIC_NUMBER = new byte[]{(byte) 'x', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;


    public static final byte HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 4 * 1024 * 1024;

    public static class MessageIdGenerator {
        private static final AtomicInteger MESSAGE_ID = new AtomicInteger(0);

        public static int nextId() {
            int prev, next;
            do {
                prev = MESSAGE_ID.get();
                next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
            } while (!MESSAGE_ID.compareAndSet(prev, next));
            return next;
        }
    }
}
