package com.ws.rpc.core.fault.retry;

import java.util.concurrent.Callable;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:21
 */
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public <T> T executeWithRetry(Callable<T> callable, int maxAttempts, long waitTime) throws Exception {
        return callable.call();
    }
}
