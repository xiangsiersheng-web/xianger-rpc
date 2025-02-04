package com.ws.rpc.core.protection.ratelimit.tokenbucket;

import com.ws.rpc.core.protection.ratelimit.RateLimiter;

/**
 * 令牌桶限流
 * @author ws
 * @version 1.0
 * @date 2025-02-04 11:03
 */
public class TokenBucketRateLimiter implements RateLimiter {
    // 配置参数
    private final long capacity;     // 令牌桶的容量
    private final double refillRate;   // 填充的令牌速率（每秒填充的令牌数量）

    // 状态参数
    private double tokens;
    private long lastRefillTime;

    public TokenBucketRateLimiter(long capacity, long refillTokens) {
        this(capacity, refillTokens, 1000);
    }

    public TokenBucketRateLimiter(long capacity, long refillTokens, long refillInterval) {
        this.capacity = capacity;
        this.refillRate = (double) refillTokens / refillInterval;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    @Override
    public boolean allowRequest() {
        synchronized (this) {
            refillTokens();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    private void refillTokens() {
        long currentTime = System.currentTimeMillis();
        double newTokens = (currentTime - lastRefillTime) * refillRate;
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTime = currentTime;
    }
}
