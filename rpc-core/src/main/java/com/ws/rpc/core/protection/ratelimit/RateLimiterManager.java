package com.ws.rpc.core.protection.ratelimit;

import com.ws.rpc.core.config.RateLimiterProperties;
import com.ws.rpc.core.enums.RateLimiterType;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.protection.ratelimit.fixedwindow.FixedWindowRateLimiter;
import com.ws.rpc.core.protection.ratelimit.leakybucket.LeakyBucketRateLimiter;
import com.ws.rpc.core.protection.ratelimit.nolimit.NoRateLimiter;
import com.ws.rpc.core.protection.ratelimit.slidingwindow.SlidingWindowRateLimiter;
import com.ws.rpc.core.protection.ratelimit.tokenbucket.TokenBucketRateLimiter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 10:46
 */
public class RateLimiterManager {
    private static final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private static RateLimiterProperties properties;
    private static boolean init = false;

    public static void init(RateLimiterProperties properties) {
        RateLimiterManager.properties = properties;
        init = true;
    }

    public static RateLimiter getRateLimiter(String rateLimiterKey) {
        // 确定限流器类型
        RateLimiterType type = RateLimiterType.fromString(properties.getStrategy());
        switch (type) {
            case NO_LIMITER:
                // 不限流是无状态的，可以使用单例
                return SingletonFactory.getInstance(NoRateLimiter.class);
            case TOKEN_BUCKET:
                return limiters.computeIfAbsent(rateLimiterKey, key -> {
                    return new TokenBucketRateLimiter(properties.getCapacity(), properties.getRefillTokens(), properties.getRefillInterval());
                });
            case FIXED_WINDOW:
                return limiters.computeIfAbsent(rateLimiterKey, key -> {
                    return new FixedWindowRateLimiter(properties.getWindowSizeMillis(), properties.getMaxRequests());
                });
            case SLIDING_WINDOW_COUNTER:
                return limiters.computeIfAbsent(rateLimiterKey, key -> {
                    return new SlidingWindowRateLimiter(properties.getWindowSizeMillis(), properties.getMaxRequests(), properties.getBucketSizeMillis());
                });
            case LEAKY_BUCKET:
                return limiters.computeIfAbsent(rateLimiterKey, key -> {
                    return new LeakyBucketRateLimiter(properties.getCapacity(), properties.getLeakRateMillis());
                });
            default:
                throw new IllegalArgumentException("Unsupported rate limiter type: " + type);
        }
    }
}
