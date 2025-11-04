package com.example.feature_home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Holds placeholder score data for the score dialog.
 */
public class ScoreDialogViewModel extends ViewModel {

    private static final String TAG = "ScoreDialogVM";

    private static final List<String> SAMPLE_SCORES = Collections.unmodifiableList(Arrays.asList(
            "최근 전적: 5승 3패",
            "최고 점수: 1230",
            "최근 랭킹: 24위"
    ));

    @NonNull
    public List<String> getScores() {
        return SAMPLE_SCORES;
    }

    public void onCloseClicked() {
        Log.d(TAG, "Score dialog closed");
    }

    public void onShareClicked() {
        Log.d(TAG, "Score share clicked");
    }
}
