package com.example.feature_home.home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Supplies stub ranking data until real integration arrives.
 */
public class RankingDialogViewModel extends ViewModel {

    private static final String TAG = "RankingDialogVM";

    private static final List<String> SAMPLE_RANKING = Collections.unmodifiableList(Arrays.asList(
            "1위 - 게스트A",
            "2위 - 게스트B",
            "3위 - 게스트C",
            "4위 - 게스트D",
            "5위 - 게스트E"
    ));

    @NonNull
    public List<String> getRankingEntries() {
        return SAMPLE_RANKING;
    }

    public void onFilterClicked(@NonNull String filterId) {
        Log.d(TAG, "Ranking filter clicked: " + filterId);
    }

    public void onCloseClicked() {
        Log.d(TAG, "Ranking dialog closed");
    }
}
