package com.example.feature_home.home.presentation.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScoreMilestone {

    private final float score;
    private final List<Integer> ruleIds;

    public ScoreMilestone(float score, @NonNull List<Integer> ruleIds) {
        this.score = score;
        this.ruleIds = Collections.unmodifiableList(new ArrayList<>(ruleIds));
    }

    public float getScore() {
        return score;
    }

    @NonNull
    public List<Integer> getRuleIds() {
        return ruleIds;
    }
}
