package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core_api.exception.UseCaseException;

import java.util.Objects;

public final class TcpAuthUseCase extends UseCase<String, UseCase.None> {

    private final RealtimeRepository realtimeRepository;

    public TcpAuthUseCase(UseCaseConfig config, RealtimeRepository realtimeRepository) {
        super(config);
        this.realtimeRepository = Objects.requireNonNull(realtimeRepository, "realtimeRepository");
    }

    @Override
    protected UseCase.None run(String input) throws UseCaseException {
        String payload = input != null ? input : "";
        try {
            realtimeRepository.auth(payload);
            return UseCase.None.INSTANCE;
        } catch (RuntimeException e) {
            throw new UseCaseException("REMOTE_FAILURE",
                    e.getMessage() != null ? e.getMessage() : "AUTH request failed", e);
        }
    }
}
