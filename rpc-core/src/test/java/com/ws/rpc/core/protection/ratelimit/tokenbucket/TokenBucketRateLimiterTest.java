package com.ws.rpc.core.protection.ratelimit.tokenbucket;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 12:41
 */
public class TokenBucketRateLimiterTest {
    private TokenBucketRateLimiter rateLimiter;

    @Before
    public void setUp() {
        // 令牌桶容量 5，每秒补充 2 个令牌
        rateLimiter = new TokenBucketRateLimiter(5, 2, 1000);
    }

    @Test
    public void testInitialTokenAvailability() {
        // 初始状态应该允许 5 次请求
        for (int i = 0; i < 5; i++) {
            assertTrue("初始令牌应足够", rateLimiter.allowRequest());
        }
        // 第 6 次请求应该被拒绝
        assertFalse("超过令牌容量的请求应被拒绝", rateLimiter.allowRequest());
    }

    @Test
    public void testTokenRefill() throws InterruptedException {
        // 消耗所有令牌
        for (int i = 0; i < 5; i++) {
            rateLimiter.allowRequest();
        }
        assertFalse("所有令牌消耗完后请求应被拒绝", rateLimiter.allowRequest());

        // 等待 1.5 秒，让令牌补充 3 个（2个/秒）
        Thread.sleep(1500);
        assertTrue("补充令牌后应允许请求", rateLimiter.allowRequest());
        assertTrue("补充令牌后应允许第二个请求", rateLimiter.allowRequest());
        assertTrue("补充令牌后应允许第三个请求", rateLimiter.allowRequest());
        assertFalse("超过补充令牌后应被拒绝", rateLimiter.allowRequest());
    }

    @Test
    public void testTokenRefillBoundary() throws InterruptedException {
        // 等待 2.5 秒，让令牌补充 5 个（但最多存 5 个）
        Thread.sleep(2500);
        // 之前的 5 个令牌应该已经满了，检查是否不会超过容量
        assertTrue("应该允许请求", rateLimiter.allowRequest());
        assertTrue("应该允许请求", rateLimiter.allowRequest());
        assertTrue("应该允许请求", rateLimiter.allowRequest());
        assertTrue("应该允许请求", rateLimiter.allowRequest());
        assertTrue("应该允许请求", rateLimiter.allowRequest());
        assertFalse("容量限制，不能超过最大令牌数", rateLimiter.allowRequest());
    }

    @Test
    public void testHighLoad() {
        // 高频调用 100 次，但桶最大容量是 5，速率是 2/s，应该不会全部通过
        int allowed = 0;
        for (int i = 0; i < 100; i++) {
            if (rateLimiter.allowRequest()) {
                allowed++;
            }
        }
        assertEquals("最多只能通过 5 个初始令牌", 5, allowed);
    }
}