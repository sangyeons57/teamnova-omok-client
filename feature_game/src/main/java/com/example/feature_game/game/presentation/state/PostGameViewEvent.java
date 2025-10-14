package com.example.feature_game.game.presentation.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.out.realtime.PostGameDecisionAck;

import java.util.Objects;

/**
 * One-shot events emitted by the post-game screen.
 */
public final class PostGameViewEvent {

    public enum Type {
        REMATCH_STARTED,
        SESSION_TERMINATED,
        SHOW_ERROR,
        EXIT_TO_HOME
    }

    private final Type type;
    private final String message;
    private final PostGameDecisionAck.ErrorReason errorReason;

    public PostGameViewEvent(@NonNull Type type) {
        this(type, null);
    }

    public PostGameViewEvent(@NonNull Type type, @Nullable String message) {
        this(type, message, null);
    }

    public PostGameViewEvent(@NonNull Type type,
                             @Nullable String message,
                             @Nullable PostGameDecisionAck.ErrorReason errorReason) {
        this.type = Objects.requireNonNull(type, "type");
        this.message = message;
        this.errorReason = errorReason;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public PostGameDecisionAck.ErrorReason getErrorReason() {
        return errorReason;
    }
}
