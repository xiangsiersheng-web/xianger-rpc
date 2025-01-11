package com.ws.rpc.core.fault.retry;

import com.ws.rpc.core.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:10
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public <T> T executeWithRetry(Callable<T> callable, int maxAttempts, long waitTime) throws Exception {
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                return callable.call();
            } catch (Exception e) {
                attempt++;
                log.warn("Remote call failed (attempt {} of {}). Retrying in {} ms.",
                        attempt, maxAttempts, waitTime);
                if (attempt >= maxAttempts) {
                    log.warn("Rpc retry {} all failed", attempt);
                    throw e;
                }
                Thread.sleep(waitTime);
            }
        }
        throw new RpcException("Rpc retry all failed");
    }
}
