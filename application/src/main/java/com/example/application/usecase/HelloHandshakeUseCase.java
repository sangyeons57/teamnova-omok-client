package com.example.application.usecase;

import android.util.Log;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core.exception.UseCaseException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class HelloHandshakeUseCase extends UseCase<String, CompletableFuture<String>> {

    private final RealtimeRepository realtimeRepository;

    public HelloHandshakeUseCase(UseCaseConfig config, RealtimeRepository realtimeRepository) {
        super(config);
        this.realtimeRepository = Objects.requireNonNull(realtimeRepository, "realtimeRepository");
    }

    @Override
    protected CompletableFuture<String> run(String input) throws UseCaseException {
        String payload = input != null ? input : "";
        return realtimeRepository.hello(payload);
    }
}
