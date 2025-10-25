package com.example.feature_home.home.presentation.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScoreMilestone {

    private final float score;
    private final List<String> ruleCodes;

    public ScoreMilestone(float score, @NonNull List<String> ruleCodes) {
        this.score = score;
        this.ruleCodes = Collections.unmodifiableList(new ArrayList<>(ruleCodes));
    }

    public float getScore() {
        return score;
    }

    @NonNull
    public List<String> getRuleCodes() {
        return ruleCodes;
    }
}
