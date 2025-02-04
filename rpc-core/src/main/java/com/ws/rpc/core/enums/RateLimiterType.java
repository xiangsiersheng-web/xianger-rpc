package com.ws.rpc.core.enums;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 12:27
 */
public enum RateLimiterType {
    /**
     * 无限流
     */
    NO_LIMITER,

    /**
     * 令牌桶算法（Token Bucket）
     * - 允许短时间内的突发流量，但有最大速率
     */
    TOKEN_BUCKET,;

    public static RateLimiterType fromString(String name) {
        for (RateLimiterType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown rete limiter type : " + name);
    }
}
