package com.example.core_di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.core.sound.SoundManager;
import com.example.infra.sound.AndroidSoundManager;

/**
 * Provides a singleton {@link SoundManager} instance backed by {@link AndroidSoundManager}.
 */
public final class SoundManagerContainer {

    private static volatile SoundManagerContainer instance;
    private final SoundManager soundManager;

    private SoundManagerContainer(@NonNull Context context) {
        this.soundManager = new AndroidSoundManager(context);
    }

    public static void init(@NonNull Context context) {
        if (instance != null) {
            return;
        }
        synchronized (SoundManagerContainer.class) {
            if (instance == null) {
                instance = new SoundManagerContainer(context.getApplicationContext());
            }
        }
    }

    @NonNull
    public static SoundManagerContainer getInstance() {
        SoundManagerContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("SoundManagerContainer is not initialized");
        }
        return container;
    }

    @NonNull
    public SoundManager getSoundManager() {
        return soundManager;
    }
}
