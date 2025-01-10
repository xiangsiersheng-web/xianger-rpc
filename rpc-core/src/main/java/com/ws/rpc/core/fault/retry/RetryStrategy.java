package com.ws.rpc.core.fault.retry;

import java.util.concurrent.Callable;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:08
 */
public interface RetryStrategy {
    <T> T executeWithRetry(Callable<T> callable, int maxAttempts, long waitTime) throws Exception;
}
