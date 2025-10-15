package com.example.core_di.sound;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.core.sound.SoundIds;
import com.example.core.sound.SoundManager;
import com.example.core_di.SoundManagerContainer;

/**
 * Convenience helpers for triggering app-wide sound effects.
 */
public final class SoundEffects {

    private static final String TAG = "SoundEffects";

    private SoundEffects() {
        // No instances.
    }

    /**
     * Plays the canonical button click sound effect.
     */
    public static void playButtonClick() {
        play(SoundIds.UI_BUTTON_CLICK);
    }

    /**
     * Plays a sound if the manager has been initialised.
     */
    public static void play(@NonNull String soundId) {
        try {
            SoundManager soundManager = SoundManagerContainer.getInstance().getSoundManager();
            soundManager.play(soundId);
        } catch (IllegalStateException exception) {
            Log.w(TAG, "Unable to play sound '" + soundId + "' because SoundManager is not initialised.", exception);
        }
    }
}
