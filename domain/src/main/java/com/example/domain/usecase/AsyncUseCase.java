package com.example.domain.usecase;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public abstract class AsyncUseCase<I, O> implements  UseCase<I, CompletableFuture<O>> {
    private final Executor executor;

    protected AsyncUseCase(Executor executor) {
        this.executor = executor;
    }

    @Override
    public final UResult<CompletableFuture<O>> execute(I input) {
        return new UResult.Ok<>(CompletableFuture.supplyAsync(() -> {
            try { return run(input); }
            catch (UseCaseException e) { throw new CompletionException(e); }
        },executor));
    }

    protected abstract O run(I input) throws UseCaseException;
}
