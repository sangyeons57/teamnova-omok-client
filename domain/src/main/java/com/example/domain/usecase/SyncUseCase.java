package com.example.domain.usecase;

import java.util.concurrent.CompletableFuture;

public abstract class SyncUseCase<I, O> implements UseCase<I, O> {
    @Override
    public final UResult<O> execute(I input){
        try { return new UResult.Ok<>(run(input)); }
        catch (UseCaseException e) { return new UResult.Err<>(e.code(), e.getMessage()); }
    }

    protected abstract O run(I input) throws UseCaseException;
}

