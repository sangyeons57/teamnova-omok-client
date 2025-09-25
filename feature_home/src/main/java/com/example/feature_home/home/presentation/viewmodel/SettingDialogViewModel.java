package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * Handles taps inside the Settings dialog.
 */
public class SettingDialogViewModel extends ViewModel {

    private static final String TAG = "SettingDialogVM";

    public void onGeneralSettingClicked(@NonNull String settingId) {
        Log.d(TAG, "Setting option clicked: " + settingId);
    }

    public void onOpenProfileClicked() {
        Log.d(TAG, "Profile settings requested");
    }

    public void onLogoutRequested() {
        Log.d(TAG, "Logout requested");
    }

    public void onWithdrawRequested() {
        Log.d(TAG, "Account deletion requested");
    }

    public void onCloseClicked() {
        Log.d(TAG, "Settings dialog closed");
    }
}
