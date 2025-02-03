package com.ws.rpc.core.protection.circuit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-03 16:28
 */
public class CircuitBreakerTest {

    private CircuitBreaker circuitBreaker;

    @Before
    public void setUp() {
        // 初始化 CircuitBreaker，假设失败阈值为 3，成功阈值为 2，最大半开请求数为 3，超时时间为 10000ms
        circuitBreaker = new CircuitBreaker(3, 2, 3, 1000);
    }

    @Test
    public void testAllowRequestInClosedState() {
        // 在 CLOSED 状态下，应该允许请求
        assertTrue(circuitBreaker.allowRequest());
    }


    @Test
    public void testOpenStateTimeoutTransitionsToHalfOpen() throws InterruptedException {
        // 模拟 3 次失败，熔断器应进入 OPEN 状态
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        assertFalse(circuitBreaker.allowRequest());

        // 等待超时，状态应该转为 HALF_OPEN
        Thread.sleep(1010);  // 等待 1 秒超时
        assertTrue(circuitBreaker.allowRequest());  // 允许请求
        assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());
    }

    @Test
    public void testAllowRequestsInHalfOpenState() throws InterruptedException {
        // 模拟 3 次失败，熔断器进入 OPEN 状态
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        // 等待超时，进入 HALF_OPEN 状态
        Thread.sleep(1001);

        // 在 HALF_OPEN 状态下，应该允许请求，最多 3 次
        assertTrue(circuitBreaker.allowRequest());  // 第 1 次请求允许
        assertTrue(circuitBreaker.allowRequest());  // 第 2 次请求允许
        assertTrue(circuitBreaker.allowRequest());  // 第 3 次请求允许
        assertFalse(circuitBreaker.allowRequest()); // 超过最大请求数，不再允许请求
    }

    @Test
    public void testSuccessfulRequestsInHalfOpenState() throws InterruptedException {
        // 模拟 3 次失败，熔断器进入 OPEN 状态
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        // 等待超时，进入 HALF_OPEN 状态
        Thread.sleep(1001);

        // 模拟成功的请求
        circuitBreaker.allowRequest();
        circuitBreaker.recordSuccess();
        circuitBreaker.recordFailure();
        circuitBreaker.recordSuccess();  // 第3次成功，熔断器应恢复为 CLOSED

        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }
}