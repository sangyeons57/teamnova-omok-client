package com.example.application.port.in;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Base contract for synchronous or asynchronous use cases that return a
 * {@link UResult} wrapper.
 */
public abstract class UseCase<I, O> {

    public UResult<O> execute(I input) {
        try { return new UResult.Ok<>(run(input)); }
        catch (UseCaseException e) { return new UResult.Err<>(e.code(), e.getMessage()); }
    }

    public CompletableFuture<UResult<O>> executeAsync(I input, Executor executor) {
        return CompletableFuture.supplyAsync(()->{
            try { return run(input); }
            catch (UseCaseException e) { throw new CompletionException(e); }
        }, executor).handle((value, ex) -> {
            if (ex == null) return new UResult.Ok<>(value);

            Throwable cause = (ex instanceof  CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

            if (cause instanceof UseCaseException ue) {
                return new UResult.Err<>(ue.code(), ue.getMessage());
            }

            return new UResult.Err<>("UNEXPECTED", ex.getMessage());
        });
    }

    protected abstract O run(I input) throws UseCaseException;

    static final class None {
        public static final None INSTANCE = new None();

        private None() {
        }
    }
}
