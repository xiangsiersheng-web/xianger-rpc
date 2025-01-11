package com.ws.rpc.core.fault.retry;

import com.ws.rpc.core.enums.RetryStrategyType;
import com.ws.rpc.core.factory.SingletonFactory;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:28
 */
public class RetryStrategyFactory {
    public static RetryStrategy getRetryStrategy(RetryStrategyType retryStrategyType) {
        // 无状态的重试策略类，可以考虑使用单例模式
        switch (retryStrategyType) {
            case FIXED_INTERVAL:
                return SingletonFactory.getInstance(FixedIntervalRetryStrategy.class);
            case EXPONENTIAL_BACKOFF:
                return SingletonFactory.getInstance(ExponentialBackoffRetryStrategy.class);
            case INFINITE:
                return SingletonFactory.getInstance(InfiniteRetryStrategy.class);
            case NO_RETRY:
                return SingletonFactory.getInstance(NoRetryStrategy.class);
            default:
                throw new RuntimeException("Unsupported retry strategy type: " + retryStrategyType);
        }
    }
}
