package com.example.application.usecase;

import static com.example.core.sound.SoundIds.SOUND_ID_PLACE_STONE;

import androidx.annotation.NonNull;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.PlaceStoneResponse;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core.exception.UseCaseException;
import com.example.core.sound.SoundManager;

/**
 * Sends a PLACE_STONE frame to the realtime server for the provided coordinates.
 */
public final class PlaceStoneUseCase extends UseCase<PlaceStoneUseCase.Params, PlaceStoneResponse> {

    private final RealtimeRepository realtimeRepository;
    private final SoundManager soundManager;

    public PlaceStoneUseCase(@NonNull UseCaseConfig useCaseConfig,
                             @NonNull RealtimeRepository realtimeRepository,
                             @NonNull SoundManager soundManager) {
        super(useCaseConfig);
        this.realtimeRepository = realtimeRepository;
        this.soundManager = soundManager;
    }

    @Override
    protected PlaceStoneResponse run(Params input) throws UseCaseException {
        soundManager.play(SOUND_ID_PLACE_STONE);
        if (input == null) {
            throw UseCaseException.of("INVALID_INPUT", "params == null");
        }
        try {
            return realtimeRepository.placeStone(input.x(), input.y()).join();
        } catch (java.util.concurrent.CompletionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof UseCaseException useCaseException) {
                throw useCaseException;
            }
            String message = cause != null && cause.getMessage() != null
                    ? cause.getMessage()
                    : "PLACE_STONE request failed";
            throw new UseCaseException("REMOTE_FAILURE", message, cause);
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : "PLACE_STONE request failed";
            throw new UseCaseException("REMOTE_FAILURE", message, e);
        }
    }

    public record Params(int x, int y) {}
}
