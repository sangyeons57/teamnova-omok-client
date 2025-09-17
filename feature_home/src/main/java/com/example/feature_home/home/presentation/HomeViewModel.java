package com.example.feature_home.home.presentation;

import android.util.Log;

import androidx.lifecycle.ViewModel;

/**
 * Home screen ViewModel handling button tap logs.
 */
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";

    public void onPrimaryButtonClicked() {
        Log.d(TAG, "Button 1 clicked");
    }

    public void onSecondaryButtonClicked() {
        Log.d(TAG, "Button 2 clicked");
    }

    public void onTertiaryButtonClicked() {
        Log.d(TAG, "Button 3 clicked");
    }

    public void onCenterButtonClicked() {
        Log.d(TAG, "Button 4 clicked");
    }

    public void onTopLeftButtonClicked() {
        Log.d(TAG, "Button 5 clicked");
    }

    public void onTopRightButtonClicked() {
        Log.d(TAG, "Button 6 clicked");
    }

    public void onTopRightSecondaryButtonClicked() {
        Log.d(TAG, "Button 7 clicked");
    }
}
