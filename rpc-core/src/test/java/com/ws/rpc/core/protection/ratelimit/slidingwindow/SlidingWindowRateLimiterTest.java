package com.ws.rpc.core.protection.ratelimit.slidingwindow;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 15:15
 */
public class SlidingWindowRateLimiterTest {
    private SlidingWindowRateLimiter rateLimiter;

    @Before
    public void setUp() {
        // 初始化一个滑动窗口限流器：窗口大小为 1000ms，最大请求数为 5，每个小窗口大小为 200ms
        rateLimiter = new SlidingWindowRateLimiter(1000, 5, 200);
    }

    @Test
    public void testAllowRequestWithinLimit() {
        // 测试未超过限制时，允许请求
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allowRequest());
        }
    }

    @Test
    public void testRejectRequestExceedLimit() {
        // 测试超过限制时，拒绝请求
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allowRequest());
        }
        assertFalse(rateLimiter.allowRequest());
    }

    @Test
    public void testWindowSliding() throws InterruptedException {
        // 测试时间窗口滑动
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allowRequest());
        }
        assertFalse(rateLimiter.allowRequest()); // 超过限制，拒绝请求

        // 等待窗口滑动（超过窗口大小）
        Thread.sleep(1100);

        // 新的窗口应该允许请求
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.allowRequest());
        }
        assertFalse(rateLimiter.allowRequest()); // 再次超过限制
    }

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        // 测试并发场景
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 记录允许的请求数
        AtomicInteger allowedRequests = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                if (rateLimiter.allowRequest()) {
                    allowedRequests.incrementAndGet();
                }
                latch.countDown();
            });
        }

        // 等待所有线程完成
        latch.await();
        executorService.shutdown();

        // 检查允许的请求数是否不超过限制
        assertTrue(allowedRequests.get() <= 5);
    }
}