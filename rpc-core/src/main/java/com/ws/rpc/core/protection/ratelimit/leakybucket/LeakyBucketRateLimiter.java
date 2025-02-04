package com.ws.rpc.core.protection.ratelimit.leakybucket;

import com.ws.rpc.core.protection.ratelimit.RateLimiter;

/**
 * 漏桶算法：固定速率放行请求
 * @author ws
 * @version 1.0
 * @date 2025-02-04 15:27
 */
public class LeakyBucketRateLimiter implements RateLimiter {
    // 配置参数
    private final long capacity;        // 桶的容量
    private final long leakRateMillis;  // 每次处理一个请求的间隔（毫秒）

    // 状态参数
    private double currentWaterLevel;   // 当前水桶中的水量
    private long lastLeakTime;          // 上次漏水时间

    public LeakyBucketRateLimiter(long capacity, long leakRateMillis) {
        this.capacity = capacity;
        this.leakRateMillis = leakRateMillis;
        this.currentWaterLevel = 0.0;
        this.lastLeakTime = System.currentTimeMillis();
    }

    @Override
    public boolean allowRequest() {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastLeakTime;

            // 计算漏水量（整数次漏水）
            long leaks = elapsedTime / leakRateMillis;
            if (leaks > 0) {
                currentWaterLevel = Math.max(0, currentWaterLevel - leaks);
                lastLeakTime += leaks * leakRateMillis; // 更新为最后一次漏水时间
            }

            if (currentWaterLevel >= capacity) {
                return false; // 桶已满，拒绝请求
            }

            currentWaterLevel++;
            return true;
        }
    }
}
