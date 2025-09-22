package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.feature_home.home.presentation.state.HomeViewEvent;

/**
 * Handles user interactions on the Home screen and exposes one-shot view events.
 */
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";

    private final MutableLiveData<HomeViewEvent> viewEvents = new MutableLiveData<>();

    public LiveData<HomeViewEvent> getViewEvents() {
        return viewEvents;
    }

    public void onBannerClicked() {
        Log.d(TAG, "Banner tapped");
    }

    public void onMatchingClicked() {
        viewEvents.setValue(HomeViewEvent.NAVIGATE_TO_MATCHING);
    }

    public void onGameModeClicked() {
        viewEvents.setValue(HomeViewEvent.SHOW_GAME_MODE_DIALOG);
    }

    public void onScoreClicked() {
        viewEvents.setValue(HomeViewEvent.SHOW_SCORE_DIALOG);
    }

    public void onRankingClicked() {
        viewEvents.setValue(HomeViewEvent.SHOW_RANKING_DIALOG);
    }

    public void onSettingsClicked() {
        viewEvents.setValue(HomeViewEvent.SHOW_SETTING_DIALOG);
    }

    public void onLogOnlyClicked() {
        Log.d(TAG, "Log-only button tapped");
    }

    public void onEventHandled() {
        viewEvents.setValue(null);
    }
}
