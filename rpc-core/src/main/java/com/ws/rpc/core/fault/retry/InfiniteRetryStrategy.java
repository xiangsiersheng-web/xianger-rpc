package com.ws.rpc.core.fault.retry;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:19
 */
@Slf4j
public class InfiniteRetryStrategy implements RetryStrategy {
    @Override
    public <T> T executeWithRetry(Callable<T> callable, int maxAttempts, long waitTime) throws Exception {
        int attempt = 0;
        while (true) {
            try {
                return callable.call();
            } catch (Exception e) {
                attempt++;
                log.warn("Remote call failed (attempt {} of Inf). Retrying in {} ms.", attempt, waitTime);
                Thread.sleep(waitTime);
            }
        }
    }
}
