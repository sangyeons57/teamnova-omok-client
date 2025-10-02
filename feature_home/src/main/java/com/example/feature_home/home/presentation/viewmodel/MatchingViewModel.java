package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.session.GameInfoStore;
import com.example.application.session.MatchState;
import com.example.application.usecase.JoinMatchUseCase;
import com.example.feature_home.home.presentation.state.MatchingViewEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Coordinates UI actions for the matching screen.
 */
public class MatchingViewModel extends ViewModel {

    private static final String TAG = "MatchingViewModel";

    private final MutableLiveData<MatchingViewEvent> viewEvents = new MutableLiveData<>();
    private final JoinMatchUseCase joinMatchUseCase;
    private final GameInfoStore gameInfoStore;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public MatchingViewModel(JoinMatchUseCase joinMatchUseCase, GameInfoStore gameInfoStore) {
        this.joinMatchUseCase = joinMatchUseCase;
        this.gameInfoStore = gameInfoStore;
        this.gameInfoStore.updateMatchState(MatchState.MATCHING);
        startMatching();
    }

    public LiveData<MatchingViewEvent> getViewEvents() {
        return viewEvents;
    }

    public LiveData<MatchState> getMatchState() {
        return gameInfoStore.getMatchStateStream();
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

    private void startMatching() {
        joinMatchUseCase.executeAsync(UseCase.None.INSTANCE, executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        // TimeoutException is expected when timeout is zero (fire-and-forget).
                        if (throwable.getCause() instanceof TimeoutException) {
                            Log.d(TAG, "Join match request sent (fire-and-forget).");
                        } else {
                            Log.e(TAG, "Failed to join match", throwable);
                        }
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "Failed to join match: " + err.message());
                    } else {
                        Log.d(TAG, "Successfully joined match");
                    }
                });
    }

    @Override
    protected void onCleared() {
        // Reset match state if the user leaves the screen before a match is found.
        if (gameInfoStore.getMatchStateStream().getValue() == MatchState.MATCHING) {
            gameInfoStore.updateMatchState(MatchState.IDLE);
        }
        executorService.shutdownNow();
        super.onCleared();
    }
}
