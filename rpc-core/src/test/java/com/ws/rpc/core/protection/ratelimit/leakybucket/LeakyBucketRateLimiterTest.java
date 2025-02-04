package com.ws.rpc.core.protection.ratelimit.leakybucket;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 15:43
 */
public class LeakyBucketRateLimiterTest {
    private LeakyBucketRateLimiter rateLimiter;

    @Before
    public void setUp() {
        // 漏桶容量 3，每 500ms 处理一个请求
        rateLimiter = new LeakyBucketRateLimiter(3, 500);
    }

    @Test
    public void testLeakyBucketRateLimiting() {
        // 允许的请求（桶还没满）
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());

        // 第 4 个请求应该被拒绝（桶满）
        assertFalse(rateLimiter.allowRequest());
    }

    @Test
    public void testLeakyBucketLeakOverTime() throws InterruptedException {
        // 先填满桶
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertFalse(rateLimiter.allowRequest()); // 超出桶容量

        // 等待 600ms（比 1 个处理时间 500ms 多，应该释放 1 个请求）
        Thread.sleep(600);

        // 现在应该可以再接受 1 个请求
        assertTrue(rateLimiter.allowRequest());
        assertFalse(rateLimiter.allowRequest()); // 但仍然不能再多
    }

    @Test
    public void testSmoothRequestHandling() throws InterruptedException {
        // 发送一个请求
        assertTrue(rateLimiter.allowRequest());

        // 等待 500ms（一个请求应该被处理）
        Thread.sleep(500);

        // 现在应该可以再接受请求
        assertTrue(rateLimiter.allowRequest());

        // 立即发送多个请求（应该受限）
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertFalse(rateLimiter.allowRequest()); // 桶已满
    }
}