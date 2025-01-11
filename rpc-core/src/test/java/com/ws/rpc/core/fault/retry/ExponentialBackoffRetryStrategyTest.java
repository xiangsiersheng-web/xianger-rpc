package com.ws.rpc.core.fault.retry;

import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;


/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:50
 */
public class ExponentialBackoffRetryStrategyTest {
    @Test
    public void testRetrySuccessOnFirstAttempt() throws Exception {
        // Arrange: 构造一个总是成功的 Callable
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                return "Success";
            }
        };

        RetryStrategy retryStrategy = new ExponentialBackoffRetryStrategy();

        // Act: 执行重试策略
        String result = retryStrategy.executeWithRetry(callable, 3, 1000);

        // Assert: 验证结果
        assertEquals("Success", result);
    }

    @Test
    public void testRetrySuccessAfterFailures() throws Exception {
        // Arrange: 构造一个会失败两次后成功的 Callable
        Callable<String> callable = new Callable<String>() {
            private int attempt = 0;

            @Override
            public String call() {
                attempt++;
                if (attempt <= 3) {
                    throw new RuntimeException("Failed attempt " + attempt);
                }
                return "Success";
            }
        };

        RetryStrategy retryStrategy = new ExponentialBackoffRetryStrategy();

        // Act: 执行重试策略
        String result = retryStrategy.executeWithRetry(callable, 4, 1000);

        // Assert: 验证结果
        assertEquals("Success", result);
    }

    @Test
    public void testRetryFailsAfterMaxAttempts() throws Exception {
        // Arrange: 构造一个总是失败的 Callable
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                throw new RuntimeException("Always fails");
            }
        };

        RetryStrategy retryStrategy = new ExponentialBackoffRetryStrategy();

        // Act & Assert: 验证抛出异常
        try {
            retryStrategy.executeWithRetry(callable, 3, 100);
            fail("Expected RuntimeException was not thrown");
        } catch (Exception e) {
            assertEquals("Always fails", e.getMessage()); // 验证异常消息
        }
    }
}