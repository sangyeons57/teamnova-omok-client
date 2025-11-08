package com.example.feature_game.game.presentation.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public PostGameViewEvent(@NonNull Type type) {
        this(type, null);
    }

    public PostGameViewEvent(@NonNull Type type, @Nullable String message) {
        this.type = Objects.requireNonNull(type, "type");
        this.message = message;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

}
