package com.example.core.dialog;

import androidx.annotation.NonNull;

/**
 * Supplies dialog registrations for a specific enum-backed dialog key set.
 */
public interface DialogProvider<T extends Enum<T>> {

    /**
     * @return the enum class that represents dialog keys handled by this provider.
     */
    @NonNull
    Class<T> getDialogKeyType();

    /**
     * Register dialog controllers and configuration with the provided registry instance.
     */
    void register(@NonNull DialogRegistry<T> registry);
}
