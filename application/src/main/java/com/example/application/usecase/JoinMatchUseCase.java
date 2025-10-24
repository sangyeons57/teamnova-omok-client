package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.application.session.GameInfoStore;
import com.example.core_api.exception.UseCaseException;

public class JoinMatchUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final RealtimeRepository realtimeRepository;
    private final GameInfoStore gameInfoStore;

    public JoinMatchUseCase(UseCaseConfig useCaseConfig, RealtimeRepository realtimeRepository, GameInfoStore gameInfoStore) {
        super(useCaseConfig);
        this.realtimeRepository = realtimeRepository;
        this.gameInfoStore = gameInfoStore;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        realtimeRepository.joinMatch(String.valueOf(gameInfoStore.getCurrentMode().getCode()));
        return null;
    }
}
