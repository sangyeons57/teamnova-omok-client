package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Manages the editable profile state within the settings profile dialog.
 */
public class SettingProfileDialogViewModel extends ViewModel {

    private static final String TAG = "SettingProfileVM";

    private final MutableLiveData<String> nickname = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> inProgress = new MutableLiveData<>(false);
    private final MutableLiveData<StatusMessage> status = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> selectedIcon = new MutableLiveData<>(null);

    public LiveData<String> getNickname() {
        return nickname;
    }

    public void onNicknameChanged(@NonNull String value) {
        nickname.setValue(value);
        Log.d(TAG, "Nickname updated: " + nickname.getValue());
    }

    public void onCloseClicked() {
        Log.d(TAG, "Profile dialog closed");
    }

    public LiveData<Boolean> isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean value) {
        inProgress.setValue(value);
    }

    public LiveData<StatusMessage> getStatus() {
        return status;
    }

    public LiveData<Integer> getSelectedIcon() {
        return selectedIcon;
    }

    public void clearStatus() {
        status.setValue(null);
    }

    public void onIconSelected(int iconCode) {
        selectedIcon.setValue(iconCode);
        Log.d(TAG, "Profile icon selected: " + iconCode);
    }

    public void clearIconSelection() {
        selectedIcon.setValue(null);
    }

    public void showSuccess(@NonNull String message) {
        status.setValue(StatusMessage.success(message));
    }

    public void showError(@NonNull String message) {
        status.setValue(StatusMessage.error(message));
    }

    public static final class StatusMessage {
        public final boolean isSuccess;
        public final String message;

        private StatusMessage(boolean isSuccess, @NonNull String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }

        public static StatusMessage success(@NonNull String message) {
            return new StatusMessage(true, message);
        }

        public static StatusMessage error(@NonNull String message) {
            return new StatusMessage(false, message);
        }
    }
}
