package com.example.core_api.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Immutable request describing which dialog to open and any arguments it needs.
 */
public final class DialogRequest<T extends Enum<T>> {

    private final T type;
    private final Bundle arguments;

    public DialogRequest(@NonNull T type, @Nullable Bundle arguments) {
        this.type = type;
        this.arguments = arguments == null ? new Bundle() : new Bundle(arguments);
    }

    @NonNull
    public T getType() {
        return type;
    }

    /**
     * Returns a defensive copy of the arguments bundle so callers cannot mutate internal state.
     */
    @NonNull
    public Bundle getArguments() {
        return new Bundle(arguments);
    }
}
