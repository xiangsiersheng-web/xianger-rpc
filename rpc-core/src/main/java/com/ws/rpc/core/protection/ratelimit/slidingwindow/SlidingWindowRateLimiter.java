package com.ws.rpc.core.protection.ratelimit.slidingwindow;

import com.ws.rpc.core.protection.ratelimit.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 15:03
 */
public class SlidingWindowRateLimiter implements RateLimiter {
    // 配置参数
    private final long windowSizeMillis; // 总窗口大小（毫秒）
    private final long maxRequests; // 最大允许的请求数
    private final int bucketSizeMillis; // 每个小窗口的时间大小（毫秒）

    // 状态参数
    private final Map<Long, AtomicInteger> counterMap; // 存储每个小窗口的请求计数

    public SlidingWindowRateLimiter(long windowSizeMillis, long maxRequests, int bucketSizeMillis) {
        this.windowSizeMillis = windowSizeMillis;
        this.maxRequests = maxRequests;
        this.bucketSizeMillis = bucketSizeMillis;
        this.counterMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest() {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();

            // 清理过期的窗口
            long earliestBucket = (currentTime - windowSizeMillis) / bucketSizeMillis;
            counterMap.keySet().removeIf(key -> key < earliestBucket);

            // 统计当前窗口的请求数
            long totalRequests = counterMap.values().stream().mapToInt(AtomicInteger::get).sum();

            if (totalRequests >= maxRequests) {
                return false;
            }

            long currentBucket = currentTime / bucketSizeMillis;
            counterMap.computeIfAbsent(currentBucket, k -> new AtomicInteger(0)).incrementAndGet();
            return true;
        }
    }
}
