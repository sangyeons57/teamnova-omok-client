package com.example.feature_home.home.presentation.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScoreMilestone {

    private final int windowIndex;
    private final float score;
    private final float lowerBoundExclusive;
    private final float upperBoundInclusive;
    private final List<String> ruleCodes;

    public ScoreMilestone(int windowIndex,
                          float score,
                          float lowerBoundExclusive,
                          float upperBoundInclusive,
                          @NonNull List<String> ruleCodes) {
        this.windowIndex = windowIndex;
        this.score = score;
        this.lowerBoundExclusive = lowerBoundExclusive;
        this.upperBoundInclusive = upperBoundInclusive;
        this.ruleCodes = Collections.unmodifiableList(new ArrayList<>(ruleCodes));
    }

    public int getWindowIndex() {
        return windowIndex;
    }

    public float getScore() {
        return score;
    }

    public float getLowerBoundExclusive() {
        return lowerBoundExclusive;
    }

    public float getUpperBoundInclusive() {
        return upperBoundInclusive;
    }

    @NonNull
    public List<String> getRuleCodes() {
        return ruleCodes;
    }

    @NonNull
    public ScoreMilestone withRuleCodes(@NonNull List<String> codes) {
        return new ScoreMilestone(windowIndex, score, lowerBoundExclusive, upperBoundInclusive, codes);
    }
}
