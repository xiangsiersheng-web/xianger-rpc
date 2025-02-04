package com.ws.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 10:56
 */
@Data
@ConfigurationProperties(prefix = "rpc.server.rate-limiter")
public class RateLimiterProperties {
    private String strategy;

    // 令牌桶
    private long capacity;
    private long refillTokens;
    private long refillInterval;

    // 漏桶
    // private long capacity;
    private long leakRateMillis;

    // 固定窗口
    private long windowSizeMillis;
    private long maxRequests;

    // 滑动窗口
    // private long windowSizeMillis;
    // private long maxRequests;
    private int bucketSizeMillis;

    public RateLimiterProperties() {
        /**
         * 默认限流配置
         */
        this.strategy = "token_bucket";
        this.capacity = 100;
        this.refillTokens = 100;
        this.refillInterval = 1000;

        this.leakRateMillis = 10;   // 10ms漏一滴

        this.windowSizeMillis = 1000;
        this.maxRequests = 100;
        this.bucketSizeMillis = 100;
    }
}
