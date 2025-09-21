package com.example.feature_home.home.presentation.viewmodel;

import android.text.TextUtils;
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

    private final MutableLiveData<String> nickname = new MutableLiveData<>("Guest");

    public LiveData<String> getNickname() {
        return nickname;
    }

    public void onNicknameChanged(@NonNull String value) {
        if (TextUtils.isEmpty(value)) {
            nickname.setValue("Guest");
        } else {
            nickname.setValue(value);
        }
        Log.d(TAG, "Nickname updated: " + nickname.getValue());
    }

    public void onCloseClicked() {
        Log.d(TAG, "Profile dialog closed");
    }
}
