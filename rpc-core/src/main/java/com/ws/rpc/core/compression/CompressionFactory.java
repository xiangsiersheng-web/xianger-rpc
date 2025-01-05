package com.ws.rpc.core.compression;

import com.ws.rpc.core.compression.gzip.GzipCompression;
import com.ws.rpc.core.compression.uncompress.UnCompression;
import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.factory.SingletonFactory;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 17:50
 */
public class CompressionFactory {
    public static Compression getCompression(CompressionType type) {
        switch (type) {
            case GZIP:
                return SingletonFactory.getInstance(GzipCompression.class);
            case UN_COMPRESSION:
                return SingletonFactory.getInstance(UnCompression.class);
            default:
                throw new IllegalArgumentException("unknown compression type: " + type);
        }
    }
}
