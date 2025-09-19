package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;

/**
 * Typed accessor for obtaining the main dialog host from an Android component.
 */
public interface MainDialogHostOwner extends DialogHostOwner<MainDialogType> {

    @NonNull
    @Override
    DialogHost<MainDialogType> getDialogHost();
}
