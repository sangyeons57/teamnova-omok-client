package com.example.feature_home.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.session.GameInfoStore;
import com.example.application.session.MatchState;
import com.example.application.usecase.JoinMatchUseCase;
import com.example.application.usecase.LeaveMatchUseCase;
import com.example.feature_home.presentation.state.MatchingViewEvent;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Coordinates UI actions for the matching screen.
 */
public class MatchingViewModel extends ViewModel {

    private static final String TAG = "MatchingViewModel";

    private final MutableLiveData<MatchingViewEvent> viewEvents = new MutableLiveData<>();
    private final JoinMatchUseCase joinMatchUseCase;
    private final LeaveMatchUseCase leaveMatchUseCase;
    private final GameInfoStore gameInfoStore;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
    private final MutableLiveData<String> elapsedTimeText = new MutableLiveData<>(formatElapsedTime(0));
    private final AtomicInteger elapsedSeconds = new AtomicInteger();
    private ScheduledFuture<?> timerFuture;

    public MatchingViewModel(JoinMatchUseCase joinMatchUseCase,
                             LeaveMatchUseCase leaveMatchUseCase,
                             GameInfoStore gameInfoStore) {
        this.joinMatchUseCase = joinMatchUseCase;
        this.leaveMatchUseCase = leaveMatchUseCase;
        this.gameInfoStore = gameInfoStore;
        this.gameInfoStore.updateMatchState(MatchState.MATCHING);
        startMatching();
        startTimer();
    }

    public LiveData<MatchingViewEvent> getViewEvents() {
        return viewEvents;
    }

    public LiveData<MatchState> getMatchState() {
        return gameInfoStore.getMatchStateStream();
    }

    public LiveData<String> getElapsedTimeText() {
        return elapsedTimeText;
    }

    public void onCancelMatchingClicked() {
        stopTimer();
        resetTimer();
        leaveMatchUseCase.executeAsync(UseCase.None.INSTANCE, executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "Failed to leave match", throwable);
                        return;
                    }
                    if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "Leave match failed: " + err.message());
                    } else {
                        Log.d(TAG, "Leave match request sent");
                    }
                });
        gameInfoStore.updateMatchState(MatchState.IDLE);
        viewEvents.setValue(MatchingViewEvent.RETURN_TO_HOME);
    }

    public void onEventHandled() {
        viewEvents.setValue(null);
    }

    public void onMatchStateUpdated(MatchState state) {
        if (state == null) {
            return;
        }
        if (state == MatchState.MATCHED) {
            stopTimer();
        } else if (state == MatchState.MATCHING) {
            ensureTimerRunning();
        }
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

    private void startTimer() {
        stopTimer();
        elapsedSeconds.set(0);
        elapsedTimeText.setValue(formatElapsedTime(0));
        timerFuture = timerExecutor.scheduleWithFixedDelay(() -> {
            int seconds = elapsedSeconds.incrementAndGet();
            elapsedTimeText.postValue(formatElapsedTime(seconds));
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void ensureTimerRunning() {
        if (timerFuture == null || timerFuture.isDone()) {
            startTimer();
        }
    }

    private void stopTimer() {
        if (timerFuture != null) {
            timerFuture.cancel(false);
            timerFuture = null;
        }
    }

    private void resetTimer() {
        elapsedSeconds.set(0);
        elapsedTimeText.setValue(formatElapsedTime(0));
    }

    private static String formatElapsedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onCleared() {
        // Reset match state if the user leaves the screen before a match is found.
        if (gameInfoStore.getMatchStateStream().getValue() == MatchState.MATCHING) {
            gameInfoStore.updateMatchState(MatchState.IDLE);
        }
        executorService.shutdownNow();
        stopTimer();
        timerExecutor.shutdownNow();
        super.onCleared();
    }
}
