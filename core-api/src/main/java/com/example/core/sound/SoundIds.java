package com.example.core.sound;

import androidx.annotation.NonNull;

/**
 * Central registry of logical sound identifiers used across the app.
 */
public final class SoundIds {

    /**
     * Click feedback for primary UI buttons.
     */
    @NonNull
    public static final String UI_BUTTON_CLICK = "ui_button_click";
    public static final String SOUND_ID_PLACE_STONE = "game_place_stone";

    private SoundIds() {
        // No instances.
    }
}
