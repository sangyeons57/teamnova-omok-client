package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameMode;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.SelfDataUseCase;
import com.example.feature_home.home.presentation.state.HomeViewEvent;
import com.example.domain.user.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles user interactions on the Home screen and exposes one-shot view events.
 */
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";

    private final MutableLiveData<HomeViewEvent> viewEvents = new MutableLiveData<>();
    private final LiveData<User> userStream;
    private final SelfDataUseCase selfDataUseCase;
    private final GameInfoStore gameInfoStore;
    private final LiveData<GameMode> gameModeStream;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public HomeViewModel(@NonNull SelfDataUseCase selfDataUseCase,
                         @NonNull UserSessionStore userSessionStore,
                         @NonNull GameInfoStore gameInfoStore) {
        this.selfDataUseCase = selfDataUseCase;
        this.gameInfoStore = gameInfoStore;
        this.userStream = userSessionStore.getUserStream();
        this.gameModeStream = gameInfoStore.getModeStream();
        refreshSelfProfile();
    }

    public LiveData<HomeViewEvent> getViewEvents() {
        return viewEvents;
    }

    public LiveData<User> getUser() {
        return userStream;
    }

    public LiveData<GameMode> getGameMode() {
        return gameModeStream;
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
        viewEvents.setValue(HomeViewEvent.NAVIGATE_TO_SCORE);
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

    public void refreshSelfProfile() {
        selfDataUseCase.executeAsync(UseCase.None.INSTANCE, executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "Failed to refresh profile: " + err.message());
                    }
                });
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }
}
