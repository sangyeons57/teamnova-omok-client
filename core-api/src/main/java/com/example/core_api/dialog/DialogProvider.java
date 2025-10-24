package com.example.core_api.dialog;

import androidx.annotation.NonNull;

/**
 * Supplies a single dialog controller binding for an enum-backed key.
 */
public interface DialogProvider<T extends Enum<T>> {

    @NonNull
    Class<T> getDialogKeyType();

    @NonNull
    T getDialogKey();

    @NonNull
    DialogController<T> createController();

    @NonNull
    default DialogConfig getDialogConfig() {
        return DialogConfig.DEFAULT;
    }
}
