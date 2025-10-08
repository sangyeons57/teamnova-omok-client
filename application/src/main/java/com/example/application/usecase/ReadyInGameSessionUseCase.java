package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core.exception.UseCaseException;

/**
 * Notifies the server that the client is ready to start the in-game session.
 */
public final class ReadyInGameSessionUseCase extends UseCase<UseCase.None, UseCase.None> {

    private final RealtimeRepository realtimeRepository;

    public ReadyInGameSessionUseCase(UseCaseConfig useCaseConfig,
                                     RealtimeRepository realtimeRepository) {
        super(useCaseConfig);
        this.realtimeRepository = realtimeRepository;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        realtimeRepository.readyInGameSession();
        return null;
    }
}
