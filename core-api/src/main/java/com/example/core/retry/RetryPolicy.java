package com.example.core.retry;

import java.time.Duration;

public interface RetryPolicy {
    // 재시도를 할지 ?
    boolean shouldRetry(Throwable t, int attempt, long elapsedMillis);

    // 재시동 작업을 멈출지 ?
    boolean shouldStop (int attempt, long elapsedMillis);

    //
    Duration nextDelay(int attempt);
}
