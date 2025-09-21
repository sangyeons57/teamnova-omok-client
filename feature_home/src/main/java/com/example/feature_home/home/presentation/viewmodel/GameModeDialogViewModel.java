package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * Captures user selections inside the Game Mode dialog for analytics.
 */
public class GameModeDialogViewModel extends ViewModel {

    private static final String TAG = "GameModeDialogVM";

    public void onModeSelected(@NonNull String modeId) {
        Log.d(TAG, "Mode selected: " + modeId);
    }

    public void onCloseClicked() {
        Log.d(TAG, "Game mode dialog closed");
    }
}
