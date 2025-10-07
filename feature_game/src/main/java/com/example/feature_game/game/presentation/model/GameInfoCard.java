package com.example.feature_game.game.presentation.model;

import androidx.annotation.StringRes;

import java.util.Objects;

/**
 * Represents a selectable information card inside the game info dialog.
 */
public final class GameInfoCard {

    private final int titleResId;
    private final int descriptionResId;

    public GameInfoCard(@StringRes int titleResId, @StringRes int descriptionResId) {
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
    }

    @StringRes
    public int getTitleResId() {
        return titleResId;
    }

    @StringRes
    public int getDescriptionResId() {
        return descriptionResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameInfoCard that = (GameInfoCard) o;
        return titleResId == that.titleResId && descriptionResId == that.descriptionResId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleResId, descriptionResId);
    }

    @Override
    public String toString() {
        return "GameInfoCard{titleResId=" + titleResId + ", descriptionResId=" + descriptionResId + '}';
    }
}
