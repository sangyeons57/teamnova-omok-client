package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Manages acknowledgement state for the account deletion confirmation dialog.
 */
public class DeleteAccountDialogViewModel extends ViewModel {

    private static final String TAG = "DeleteAccountVM";

    private final MutableLiveData<CharSequence> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> acknowledged = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> inProgress = new MutableLiveData<>(false);

    public void initialize(@NonNull CharSequence defaultMessage) {
        if (message.getValue() == null) {
            message.setValue(defaultMessage);
        }
    }

    public LiveData<CharSequence> getMessage() {
        return message;
    }

    public void setMessage(@NonNull CharSequence value) {
        message.setValue(value);
        Log.d(TAG, "Message updated");
    }

    public LiveData<Boolean> isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean value) {
        acknowledged.setValue(value);
        Log.d(TAG, "Acknowledged: " + value);
    }

    public LiveData<Boolean> isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean value) {
        inProgress.setValue(value);
        Log.d(TAG, "Progress state: " + value);
    }

    public void onCloseClicked() {
        Log.d(TAG, "Delete account dialog closed");
    }

    public void onActionCompleted() {
        Log.d(TAG, "Account deletion confirmed");
    }
}

