package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core_api.exception.UseCaseException;

public class LeaveMatchUseCase extends UseCase<UseCase.None, UseCase.None> {
    private final RealtimeRepository realtimeRepository;

    public LeaveMatchUseCase(UseCaseConfig useCaseConfig, RealtimeRepository realtimeRepository) {
        super(useCaseConfig);
        this.realtimeRepository = realtimeRepository;
    }

    @Override
    protected None run(None input) throws UseCaseException {
        realtimeRepository.leaveMatch();
        return null;
    }
}
