package com.example.application.usecase;

import androidx.annotation.NonNull;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core.exception.UseCaseException;

/**
 * Sends a PLACE_STONE frame to the realtime server for the provided coordinates.
 */
public final class PlaceStoneUseCase extends UseCase<PlaceStoneUseCase.Params, UseCase.None> {

    private final RealtimeRepository realtimeRepository;

    public PlaceStoneUseCase(@NonNull UseCaseConfig useCaseConfig,
                             @NonNull RealtimeRepository realtimeRepository) {
        super(useCaseConfig);
        this.realtimeRepository = realtimeRepository;
    }

    @Override
    protected None run(Params input) throws UseCaseException {
        if (input == null) {
            throw UseCaseException.of("INVALID_INPUT", "params == null");
        }
        realtimeRepository.placeStone(input.x(), input.y());
        return null;
    }

    public record Params(int x, int y) {}
}
