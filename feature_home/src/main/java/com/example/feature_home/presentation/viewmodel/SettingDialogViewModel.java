package com.example.feature_home.presentation.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.dto.command.LinkGoogleAccountCommand;
import com.example.application.port.in.UResult;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.LinkGoogleAccountUseCase;
import com.example.application.wrapper.UserSession;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles taps inside the Settings dialog.
 */
public class SettingDialogViewModel extends ViewModel {

    private static final String TAG = "SettingDialogVM";
    private static final String ERROR_FALLBACK_MESSAGE = "구글 계정 연동에 실패했습니다.";
    private static final String SUCCESS_MESSAGE = "구글 계정과 연동되었습니다.";

    private final LinkGoogleAccountUseCase linkGoogleAccountUseCase;
    private final UserSessionStore userSessionStore;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MediatorLiveData<Boolean> googleLinked = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> googleLinkInProgress = new MutableLiveData<>(false);
    private final MediatorLiveData<Boolean> googleButtonEnabled = new MediatorLiveData<>();
    private final MutableLiveData<SettingDialogEvent> events = new MutableLiveData<>();

    public SettingDialogViewModel(@NonNull LinkGoogleAccountUseCase linkGoogleAccountUseCase,
                                  @NonNull UserSessionStore userSessionStore) {
        this.linkGoogleAccountUseCase = Objects.requireNonNull(linkGoogleAccountUseCase, "linkGoogleAccountUseCase");
        this.userSessionStore = Objects.requireNonNull(userSessionStore, "userSessionStore");

        googleLinked.setValue(resolveGoogleLinked(userSessionStore.getCurrentSession()));
        googleLinked.addSource(userSessionStore.getSessionStream(), session -> {
            googleLinked.setValue(resolveGoogleLinked(session));
        });

        googleButtonEnabled.addSource(googleLinked, ignored -> updateGoogleButtonEnabled());
        googleButtonEnabled.addSource(googleLinkInProgress, ignored -> updateGoogleButtonEnabled());
        updateGoogleButtonEnabled();
    }

    public void onGoogleSettingClicked() {
        if (Boolean.TRUE.equals(googleLinkInProgress.getValue()) || Boolean.TRUE.equals(googleLinked.getValue())) {
            return;
        }
        googleLinkInProgress.setValue(true);
        events.setValue(SettingDialogEvent.requestGoogleSignIn());
    }

    public void onGoogleCredentialReceived(@NonNull String providerIdToken) {
        if (providerIdToken.trim().isEmpty()) {
            onGoogleSignInFailed(ERROR_FALLBACK_MESSAGE);
            return;
        }

        linkGoogleAccountUseCase.executeAsync(LinkGoogleAccountCommand.of(providerIdToken), executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        googleLinkInProgress.postValue(false);
                        events.postValue(SettingDialogEvent.error(resolveMessage(throwable.getMessage())));
                        return;
                    }

                    if (result instanceof UResult.Ok<?>) {
                        googleLinkInProgress.postValue(false);
                        googleLinked.postValue(true);
                        events.postValue(SettingDialogEvent.success(SUCCESS_MESSAGE));
                    } else if (result instanceof UResult.Err<?> err) {
                        googleLinkInProgress.postValue(false);
                        events.postValue(SettingDialogEvent.error(resolveMessage(err.message())));
                    } else {
                        googleLinkInProgress.postValue(false);
                        events.postValue(SettingDialogEvent.error(ERROR_FALLBACK_MESSAGE));
                    }
                });
    }

    public void onGoogleSignInFailed(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            googleLinkInProgress.setValue(false);
            events.setValue(SettingDialogEvent.error(resolveMessage(message)));
        });
    }

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

    public LiveData<Boolean> isGoogleLinked() {
        return googleLinked;
    }

    public LiveData<Boolean> isGoogleLinkInProgress() {
        return googleLinkInProgress;
    }

    public LiveData<Boolean> isGoogleButtonEnabled() {
        return googleButtonEnabled;
    }

    public LiveData<SettingDialogEvent> getEvents() {
        return events;
    }

    public UserSessionStore getUserSessionStore() {
        return userSessionStore;
    }

    public void onEventHandled() {
        events.setValue(null);
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }

    private void updateGoogleButtonEnabled() {
        boolean enabled = !Boolean.TRUE.equals(googleLinked.getValue())
                && !Boolean.TRUE.equals(googleLinkInProgress.getValue());
        googleButtonEnabled.setValue(enabled);
    }

    private boolean resolveGoogleLinked(UserSession session) {
        return session != null && session.isGoogleLinked();
    }

    private String resolveMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ERROR_FALLBACK_MESSAGE;
        }
        return message;
    }

    public record SettingDialogEvent(@NonNull Type type, String message) {

        public enum Type {
            REQUEST_GOOGLE_SIGN_IN,
            SHOW_SUCCESS,
            SHOW_ERROR
        }

        public static SettingDialogEvent requestGoogleSignIn() {
            return new SettingDialogEvent(Type.REQUEST_GOOGLE_SIGN_IN, null);
        }

        public static SettingDialogEvent success(String message) {
            return new SettingDialogEvent(Type.SHOW_SUCCESS, message);
        }

        public static SettingDialogEvent error(String message) {
            return new SettingDialogEvent(Type.SHOW_ERROR, message);
        }
    }
}