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
    TOKEN_BUCKET,

    /**
     * 漏桶算法（Leaky Bucket）
     * - 以固定速率处理请求，平滑流量
     */
    LEAKY_BUCKET,

    /**
     * 固定窗口计数器（Fixed Window Counter）
     * - 在固定时间窗口内计数，适合简单限流需求
     */
    FIXED_WINDOW,

    /**
     * 滑动窗口计数器（Sliding Window Counter）
     * - 解决固定窗口的问题，使流量分布更均匀
     */
    SLIDING_WINDOW_COUNTER,
    ;

    public static RateLimiterType fromString(String name) {
        for (RateLimiterType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("unknown rete limiter type : " + name);
    }
}
