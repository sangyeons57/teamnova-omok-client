package com.example.core.dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

/**
 * Creates an {@link AlertDialog} instance for the given request.
 */
public interface DialogController<T extends Enum<T>> {

    @NonNull
    AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<T> request);
}
