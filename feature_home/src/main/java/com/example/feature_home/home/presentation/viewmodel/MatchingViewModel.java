package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.feature_home.home.presentation.state.MatchingViewEvent;

/**
 * Coordinates UI actions for the matching screen.
 */
public class MatchingViewModel extends ViewModel {

    private static final String TAG = "MatchingViewModel";

    private final MutableLiveData<MatchingViewEvent> viewEvents = new MutableLiveData<>();

    public LiveData<MatchingViewEvent> getViewEvents() {
        return viewEvents;
    }

    public void onBannerClicked() {
        Log.d(TAG, "Matching banner tapped");
    }

    public void onReturnHomeClicked() {
        viewEvents.setValue(MatchingViewEvent.RETURN_TO_HOME);
    }

    public void onEventHandled() {
        viewEvents.setValue(null);
    }
}
