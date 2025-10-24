package com.example.core_api.rules;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Immutable metadata describing a single optional game rule.
 */
public final class RuleInfo {

    public static final int NO_ICON = 0;

    private final int id;
    private final int nameRes;
    private final int iconRes;
    private final int descriptionRes;

    public RuleInfo(int id,
                    @StringRes int nameRes,
                    @DrawableRes int iconRes,
                    @StringRes int descriptionRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.iconRes = iconRes;
        this.descriptionRes = descriptionRes;
    }

    public int getId() {
        return id;
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    @DrawableRes
    public int getIconRes() {
        return iconRes;
    }

    @StringRes
    public int getDescriptionRes() {
        return descriptionRes;
    }

    public boolean hasIcon() {
        return iconRes != NO_ICON;
    }

    @NonNull
    @Override
    public String toString() {
        return "RuleInfo{" +
                "id=" + id +
                ", nameRes=" + nameRes +
                ", iconRes=" + iconRes +
                ", descriptionRes=" + descriptionRes +
                '}';
    }
}
