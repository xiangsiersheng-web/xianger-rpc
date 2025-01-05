package com.ws.rpc.core.compression.uncompress;

import com.ws.rpc.core.compression.Compression;

/**
 * 什么也不干，不做任何压缩
 * @author ws
 * @version 1.0
 * @date 2025-01-05 18:02
 */
public class UnCompression implements Compression {
    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }
}
