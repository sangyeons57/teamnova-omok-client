package com.example.feature_home.home.presentation.model;

import androidx.annotation.DrawableRes;

public final class ScoreMilestone {

    private final float score;
    private final int iconRes;

    public ScoreMilestone(float score, @DrawableRes int iconRes) {
        this.score = score;
        this.iconRes = iconRes;
    }

    public float getScore() {
        return score;
    }

    @DrawableRes
    public int getIconRes() {
        return iconRes;
    }
}
