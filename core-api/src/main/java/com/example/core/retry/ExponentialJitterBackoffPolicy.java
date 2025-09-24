package com.example.core.retry;

import com.example.core.exception.UseCaseException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.Duration;

public final class ExponentialJitterBackoffPolicy implements RetryPolicy{
    private final int maxAttempts;
    private final Duration baseDelay;
    private final Duration maxDelay;
    private final long maxTotalMillis;
    private final double jitterRatio;

    public ExponentialJitterBackoffPolicy (int maxAttempts, Duration baseDelay, Duration maxDelay, long maxTotalMillis, double jitterRatio) {
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
        this.maxDelay = maxDelay;
        this.maxTotalMillis = maxTotalMillis;
        this.jitterRatio = jitterRatio;
    }

    public ExponentialJitterBackoffPolicy (int maxAttempts, Duration baseDelay, Duration maxDelay, Duration maxTotalMillis, double jitterRatio) {
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
        this.maxDelay = maxDelay;
        this.maxTotalMillis = maxTotalMillis.toMillis();
        this.jitterRatio = jitterRatio;
    }

    @Override
    public boolean shouldRetry(Throwable t, int attempt, long elapsedMillis) {
        if (t instanceof InterruptedException) return false;
        if (t instanceof SocketTimeoutException) return true;
        if (t instanceof IOException) return true;
        // if (t instanceof UseCaseException) return true;
        return false;
    }

    @Override
    public boolean shouldStop(int attempt, long elapsedMillis) {
        return attempt >= maxAttempts || elapsedMillis >= maxTotalMillis;
    }

    @Override
    public Duration nextDelay(int attempt) {
        long base = (long) (baseDelay.toMillis() * Math.pow(2, attempt - 1));
        long capped = Math.min(base, maxDelay.toMillis());
        long jitter = (long) (capped * jitterRatio * Math.random());
        return Duration.ofMillis( Math.min(capped + jitter, maxDelay.toMillis()) );
    }
}
