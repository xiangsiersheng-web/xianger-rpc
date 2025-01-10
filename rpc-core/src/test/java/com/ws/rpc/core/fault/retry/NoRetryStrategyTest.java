package com.ws.rpc.core.fault.retry;

import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 23:05
 */
public class NoRetryStrategyTest {
    @Test
    public void testExecuteWithRetrySuccess() throws Exception {
        // Arrange: 模拟一个成功的 Callable
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                return "Success";
            }
        };

        RetryStrategy retryStrategy = new NoRetryStrategy();

        // Act: 调用策略
        String result = retryStrategy.executeWithRetry(callable, 3, 100);

        // Assert: 验证结果
        assertEquals("Success", result);
    }

    @Test
    public void testNoRetry() {
        // Arrange: 模拟一个失败的 Callable
        Callable<String> callable = new Callable<String>() {
            private int attempt = 0;

            @Override
            public String call() {
                attempt++;
                throw new RuntimeException("Attempt " + attempt + " failed");
            }
        };

        RetryStrategy retryStrategy = new NoRetryStrategy();

        try {
            // Act: 调用策略
            retryStrategy.executeWithRetry(callable, 3, 100);
            fail("Expected RuntimeException was not thrown");
        } catch (Exception e) {
            // Assert: 验证异常消息
            assertEquals("Attempt 1 failed", e.getMessage());
        }
    }
}