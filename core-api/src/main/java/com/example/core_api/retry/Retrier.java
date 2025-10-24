package com.example.core_api.retry;

import android.util.Log;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Retrier {
    private final String TAG = "Retrier";
    private final RetryPolicy policy;
    private final String uuid = " [" + UUID.randomUUID() + "] ";

    public Retrier (RetryPolicy policy) {
        this.policy = policy;
    }

    /** @noinspection BusyWait*/
    public <T> T run (Callable<T> task) throws Exception {
        stressLog("Start retry (sync mode)");
        long start = System.currentTimeMillis();
        int attempt = 0;
        while (true) {
            attempt++;
            log("Try " + attempt + " time");
            try { return task.call(); }
            catch (Throwable t) {
                long elapsed = System.currentTimeMillis() - start;
                if (!policy.shouldRetry(t, attempt, elapsed) || policy.shouldStop(attempt, elapsed)){
                    if (t instanceof Exception e) throw e;
                    throw new RuntimeException(t);
                }
                Thread.sleep(policy.nextDelay(attempt).toMillis());
            }
        }
    }

    public <T> CompletableFuture<T> runAsync (Supplier<CompletableFuture<T>> task, ScheduledExecutorService scheduler) {
        stressLog("Start retry (Async mode)");
        CompletableFuture<T> promise = new CompletableFuture<>();
        long start = System.currentTimeMillis();
        attemptAsync(task, scheduler, promise, start, 1);
        return promise;
    }

    private <T> void attemptAsync (Supplier<CompletableFuture<T>> task, ScheduledExecutorService scheduler, CompletableFuture<T> promise, long start, int attempt) {
        log("Try " + attempt + " time");
        task.get().whenComplete((val, err) -> {
            if (promise.isDone()) return;
            if (err == null) {
                promise.complete(val);
                return;
            }

            long elapsed = System.currentTimeMillis() - start;
            if (!policy.shouldRetry(err, attempt, elapsed) || policy.shouldStop(attempt, elapsed)) {
                promise.completeExceptionally(err);
                return;
            }
            scheduler.schedule(() ->
                    attemptAsync(task, scheduler, promise, start, attempt + 1),
                    policy.nextDelay(attempt).toMillis(), TimeUnit.MILLISECONDS);
        });
    }

    private void log(String msg) {
        Log.d(TAG, uuid + msg);
    }

    private void stressLog(String msg) {
        Log.d(TAG, "===== " +  uuid + msg + " ======");
    }
}
