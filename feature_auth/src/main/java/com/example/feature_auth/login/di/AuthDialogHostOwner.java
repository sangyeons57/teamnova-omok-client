package com.example.feature_auth.login.di;

import androidx.annotation.NonNull;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.feature_auth.login.presentation.AuthDialogType;

/**
 * Typed accessor for obtaining the auth dialog host from an Android component.
 */
public interface AuthDialogHostOwner extends DialogHostOwner<AuthDialogType> {

    @NonNull
    @Override
    DialogHost<AuthDialogType> getDialogHost();
}
