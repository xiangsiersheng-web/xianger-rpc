package com.ws.rpc.core.protection.ratelimit.fixedwindow;

import com.ws.rpc.core.protection.ratelimit.RateLimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 固定窗口限流器
 * @author ws
 * @version 1.0
 * @date 2025-02-04 14:56
 */
public class FixedWindowRateLimiter implements RateLimiter {
    // 配置参数
    private final long windowSizeMillis; // 窗口大小（毫秒）
    private final long maxRequests; // 每个窗口最大允许的请求数

    // 状态参数
    private long windowStartTime;
    private final AtomicLong requestCount;

    public FixedWindowRateLimiter(long windowSizeMillis, long maxRequests) {
        this.windowSizeMillis = windowSizeMillis;
        this.maxRequests = maxRequests;
        this.windowStartTime = System.currentTimeMillis();
        this.requestCount = new AtomicLong(0);
    }

    @Override
    public boolean allowRequest() {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - windowStartTime >= windowSizeMillis) {
                windowStartTime = currentTime;
                requestCount.set(0);
            }

            if (requestCount.incrementAndGet() <= maxRequests) {
                return true;
            }
            return false;
        }
    }
}
