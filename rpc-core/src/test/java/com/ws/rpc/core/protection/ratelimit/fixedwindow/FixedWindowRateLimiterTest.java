package com.ws.rpc.core.protection.ratelimit.fixedwindow;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 15:01
 */
public class FixedWindowRateLimiterTest {
    private FixedWindowRateLimiter rateLimiter;

    @Before
    public void setUp() {
        // 1 秒窗口，最大允许 3 次请求
        rateLimiter = new FixedWindowRateLimiter(1000, 3);
    }

    @Test
    public void testFixedWindowRateLimiting() {
        // 允许的请求
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());

        // 超出限流阈值，应返回 false
        assertFalse(rateLimiter.allowRequest());
    }

    @Test
    public void testWindowReset() throws InterruptedException {
        // 先用完 3 个令牌
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertFalse(rateLimiter.allowRequest()); // 超出限流

        // 等待 1.1 秒，窗口应重置
        Thread.sleep(1100);

        // 重新允许 3 个请求
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertTrue(rateLimiter.allowRequest());
        assertFalse(rateLimiter.allowRequest()); // 超出限流
    }
}