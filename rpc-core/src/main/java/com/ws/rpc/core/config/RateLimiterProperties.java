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

    public RateLimiterProperties() {
        /**
         * 默认限流配置
         */
        this.strategy = "token_bucket";
        this.capacity = 100;
        this.refillTokens = 100;
        this.refillInterval = 1000;
    }
}
