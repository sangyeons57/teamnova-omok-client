package com.example.core_api.dialog;

import androidx.annotation.NonNull;

/**
 * Contract for components that expose a {@link DialogHost} instance.
 */
public interface DialogHostOwner<T extends Enum<T>> {

    @NonNull
    DialogHost<T> getDialogHost();
}
