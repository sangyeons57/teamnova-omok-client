package com.example.core.dialog;

import androidx.annotation.NonNull;

/**
 * Lightweight configuration applied to every dialog created through the manager.
 */
public final class DialogConfig {

    public static final DialogConfig DEFAULT = new DialogConfig(true, true);

    private final boolean cancelable;
    private final boolean cancelOnTouchOutside;

    private DialogConfig(boolean cancelable, boolean cancelOnTouchOutside) {
        this.cancelable = cancelable;
        this.cancelOnTouchOutside = cancelOnTouchOutside;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public boolean shouldCancelOnTouchOutside() {
        return cancelOnTouchOutside;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean cancelable = true;
        private boolean cancelOnTouchOutside = true;

        @NonNull
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        @NonNull
        public Builder setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
            this.cancelOnTouchOutside = cancelOnTouchOutside;
            return this;
        }

        @NonNull
        public DialogConfig build() {
            return new DialogConfig(cancelable, cancelOnTouchOutside);
        }
    }
}
