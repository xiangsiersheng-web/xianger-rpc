package com.ws.rpc.core.compression;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 17:50
 */
public interface Compression {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
