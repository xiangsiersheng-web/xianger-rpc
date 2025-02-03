package com.ws.rpc.core.protection.circuit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器
 * @author ws
 * @version 1.0
 * @date 2025-02-03 15:38
 */
@Slf4j
public class CircuitBreaker {
    public enum State {
        /**
         * 熔断器状态
         */
        CLOSED, OPEN, HALF_OPEN
    }

    // 配置参数
    private final int failureThreshold; // 熔断器失败阈值(连续失败次数)
    private final int successThreshold; // 熔断器成功阈值(熔断器半开后，需要成功多少次才进入关闭状态)
    private final int maxHalfOpenRequests;  // 半开状态最大请求数
    private final long timeout;  // 打开状态持续时间

    // 运行时状态
    @Getter
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private volatile long lastFailureTime = 0;  // 记录最后一次失败时间(即断路器打开时的时间)
    private volatile int halfOpenRequests = 0;  // 半开状态请求数
    private final Object lock = new Object();

    public CircuitBreaker(int failureThreshold, int successThreshold) {
        this(failureThreshold, successThreshold, successThreshold, 10_000);
    }

    public CircuitBreaker(int failureThreshold, int successThreshold, long timeout) {
        this(failureThreshold, successThreshold, successThreshold, timeout);
    }

    public CircuitBreaker(int failureThreshold, int successThreshold, int maxHalfOpenRequests, long timeout) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.maxHalfOpenRequests = maxHalfOpenRequests;
        this.timeout = timeout;
    }

    public boolean allowRequest() {
        if (state == State.CLOSED) {
            return true;
        }

        // 考虑线程并发
        synchronized(lock) {
            switch(state) {
                case CLOSED:
                    return true;
                case OPEN:
                    if (System.currentTimeMillis() - lastFailureTime > timeout) {
                        // 进入半开状态
                        log.debug("CircuitBreaker state changes from open to half open.");
                        transitionToHalfOpen();
                        return true;
                    }
                    return false;
                case HALF_OPEN:
                    if (halfOpenRequests < maxHalfOpenRequests) {
                        // 未达到半开最大请求数，放行
                        halfOpenRequests++;
                        return true;
                    }
                    return false;
            }
        }
        return false;
    }

    public void recordFailure() {
        if (state == State.OPEN) {
            // 熔断器处于打开状态，不记录失败次数
            return ;
        }

        lastFailureTime = System.currentTimeMillis();
        synchronized (lock) {
            if (state == State.CLOSED && failureCount.incrementAndGet() >= failureThreshold) {
                // 熔断器打开
                log.debug("CircuitBreaker state changes from closed to open.");
                open();
            } else if (state == State.HALF_OPEN && failureCount.incrementAndGet() > maxHalfOpenRequests) {
                // 半开状态，失败次数达到最大值，熔断器打开
                log.debug("CircuitBreaker state changes from half open to open.");
                open();
            }
        }
    }

    public void recordSuccess() {
        if (state == State.OPEN) {
            // 熔断器处于打开状态，不记录成功次数
            return ;
        }

        synchronized (lock) {
            if (state == State.HALF_OPEN && successCount.incrementAndGet() >= successThreshold) {
                log.debug("CircuitBreaker state changes from half open to closed.");
                reset();
            } else if (state == State.CLOSED) {
                // 熔断器处于关闭状态，重置失败次数
                failureCount.set(0);
            }
        }
    }

    private void open() {
        state = State.OPEN;
        failureCount.set(0);
        successCount.set(0);
    }

    private void transitionToHalfOpen() {
        state = State.HALF_OPEN;
        halfOpenRequests = 1;
        successCount.set(0);
        failureCount.set(0);
    }

    private void reset() {
        state = State.CLOSED;
        successCount.set(0);
        failureCount.set(0);
    }

}
