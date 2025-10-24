package com.example.application.usecase;

import androidx.annotation.NonNull;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core_api.exception.UseCaseException;

import java.util.Objects;
import java.util.concurrent.CompletionException;

/**
 * Sends the player's post-game decision (rematch or leave) to the realtime server.
 */
public final class PostGameDecisionUseCase extends UseCase<PostGameDecisionUseCase.Params, PostGameDecisionAck> {

    private final RealtimeRepository realtimeRepository;

    public PostGameDecisionUseCase(@NonNull UseCaseConfig config,
                                   @NonNull RealtimeRepository realtimeRepository) {
        super(config);
        this.realtimeRepository = Objects.requireNonNull(realtimeRepository, "realtimeRepository");
    }

    @Override
    protected PostGameDecisionAck run(Params input) throws UseCaseException {
        if (input == null || input.decision == null) {
            throw UseCaseException.of("INVALID_INPUT", "decision == null");
        }
        if (input.decision == PostGameDecisionOption.UNKNOWN) {
            throw UseCaseException.of("INVALID_INPUT", "decision must be REMATCH or LEAVE");
        }
        try {
            return realtimeRepository.postGameDecision(input.decision).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof UseCaseException useCaseException) {
                throw useCaseException;
            }
            String message = cause != null && cause.getMessage() != null
                    ? cause.getMessage()
                    : "POST_GAME_DECISION request failed";
            throw new UseCaseException("REMOTE_FAILURE", message, cause);
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : "POST_GAME_DECISION request failed";
            throw new UseCaseException("REMOTE_FAILURE", message, e);
        }
    }

    public record Params(@NonNull PostGameDecisionOption decision) {
        public Params {
            Objects.requireNonNull(decision, "decision");
        }
    }
}
