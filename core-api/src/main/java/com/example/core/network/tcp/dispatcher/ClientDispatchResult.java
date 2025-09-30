package com.example.core.network.tcp.dispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Outcome returned by {@link com.example.core.network.tcp.handler.ClientFrameHandler} executions.
 */
public final class ClientDispatchResult {
    private static final ClientDispatchResult CONTINUE = new ClientDispatchResult(false, false, false, List.of());

    private final boolean removeHandler;
    private final boolean unregisterType;
    private final boolean stopPropagation;
    private final List<ClientDispatchEffect> effects;

    private ClientDispatchResult(boolean removeHandler,
                                 boolean unregisterType,
                                 boolean stopPropagation,
                                 List<ClientDispatchEffect> effects) {
        this.removeHandler = removeHandler;
        this.unregisterType = unregisterType;
        this.stopPropagation = stopPropagation;
        this.effects = effects.isEmpty() ? List.of() : Collections.unmodifiableList(effects);
    }

    public static ClientDispatchResult continueDispatch() {
        return CONTINUE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ClientDispatchResult buildRemoveHandler() {
        return builder().removeHandler().build();
    }

    public static ClientDispatchResult buildUnregisterType() {
        return builder().unregisterType().build();
    }

    public static ClientDispatchResult buildStopPropagation() {
        return builder().stopPropagation().build();
    }

    public boolean removeHandler() {
        return removeHandler;
    }

    public boolean unregisterType() {
        return unregisterType;
    }

    public boolean stopPropagation() {
        return stopPropagation;
    }

    public List<ClientDispatchEffect> effects() {
        return effects;
    }

    public static final class Builder {
        private boolean removeHandler;
        private boolean unregisterType;
        private boolean stopPropagation;
        private final List<ClientDispatchEffect> effects = new ArrayList<>();

        private Builder() {
        }

        public Builder removeHandler() {
            this.removeHandler = true;
            return this;
        }

        public Builder unregisterType() {
            this.unregisterType = true;
            return this;
        }

        public Builder stopPropagation() {
            this.stopPropagation = true;
            return this;
        }

        public Builder addEffect(ClientDispatchEffect effect) {
            effects.add(Objects.requireNonNull(effect, "effect"));
            return this;
        }

        public ClientDispatchResult build() {
            return new ClientDispatchResult(removeHandler, unregisterType, stopPropagation, effects);
        }
    }
}
