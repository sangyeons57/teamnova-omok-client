package com.example.application.port.in;

import com.example.core.exception.UseCaseException;
import com.example.core.retry.Retrier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Base contract for synchronous or asynchronous use cases that return a
 * {@link UResult} wrapper.
 */
public abstract class UseCase<I, O> {

    protected UseCaseConfig config;
    protected Retrier retrier;

    public UseCase(UseCaseConfig useCaseConfig) {
        this.config = useCaseConfig;
        this.retrier = new Retrier(config.retryPolicy);
    }

    public UResult<O> execute(I input) {
        return execute(input, UseCaseOptions.builder().build());
    }

    public UResult<O> execute(I input, UseCaseOptions callOptions) {
        try {
            O out = callOptions.getRetryEnabled(config.retryEnabledByDefault)
                        ? retrier.run(()-> run(input))
                        : run(input);
            return new UResult.Ok<>(out);
        } catch (UseCaseException e) {
            return new UResult.Err<>(e.code(), e.getMessage());
        } catch (Exception e) {
            return new UResult.Err<>("UNEXPECTED", e.getMessage());
        }
    }

    public CompletableFuture<UResult<O>> executeAsync(I input, Executor executor) {
        return executeAsync(input, executor, UseCaseOptions.builder().build());
    }

    public CompletableFuture<UResult<O>> executeAsync(I input, Executor executor, UseCaseOptions callOptions) {
        Supplier<CompletableFuture<O>> task =
                () -> CompletableFuture.supplyAsync(()->{
                    try { return run(input); }
                    catch (UseCaseException e) { throw new CompletionException(e); }
                },executor);

        CompletableFuture<O> cf = callOptions.getRetryEnabled(config.retryEnabledByDefault)
                ? retrier.runAsync(task, callOptions.getScheduler(config.scheduler))
                : task.get();

        return cf.handle((value, ex) -> {
            if (ex == null) return new UResult.Ok<>(value);

            Throwable cause = (ex instanceof  CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            if (cause instanceof UseCaseException ue) {
                return new UResult.Err<>(ue.code(), ue.getMessage());
            }

            return new UResult.Err<>("UNEXPECTED", ex.getMessage());
        });
    }

    protected abstract O run(I input) throws UseCaseException;

    /** @noinspection InstantiationOfUtilityClass*/
    static public final class None {
        public static final None INSTANCE = new None();

        private None() {
        }
    }
}
