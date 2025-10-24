package com.example.application.port.in;

import com.example.core_api.retry.ExponentialJitterBackoffPolicy;
import com.example.core_api.retry.RetryPolicy;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class UseCaseConfig {
    public final boolean retryEnabledByDefault;
    public final RetryPolicy retryPolicy;
    public final ScheduledExecutorService scheduler;

    public UseCaseConfig(boolean retryEnabledByDefault, RetryPolicy retryPolicy, ScheduledExecutorService scheduler) {
        this.retryEnabledByDefault = retryEnabledByDefault;
        this.retryPolicy = retryPolicy;
        this.scheduler = scheduler;
    }

    public static UseCaseConfig defaultConfig() {
        return new UseCaseConfig(
                true,
                new ExponentialJitterBackoffPolicy(
                        5, Duration.ofMillis(200), Duration.ofSeconds(3),  10_000, 0.3
                ),
                Executors.newScheduledThreadPool(1)
        );
    }

}
