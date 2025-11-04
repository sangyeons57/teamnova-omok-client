package com.example.infra.sound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.sound.SoundManager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks registered {@link SoundManager.SoundRegistration} entries and their associated SoundPool
 * sample identifiers.
 */
final class SoundRegistry {

    private final Map<String, SoundManager.SoundRegistration> registrations = new ConcurrentHashMap<>();
    private final Map<String, Integer> sampleIds = new ConcurrentHashMap<>();

    @Nullable
    Integer register(@NonNull SoundManager.SoundRegistration registration, int sampleId) {
        registrations.put(registration.getSoundId(), registration);
        return sampleIds.put(registration.getSoundId(), sampleId);
    }

    @Nullable
    SoundManager.SoundRegistration getRegistration(@NonNull String soundId) {
        return registrations.get(soundId);
    }

    @Nullable
    Integer getSampleId(@NonNull String soundId) {
        return sampleIds.get(soundId);
    }

    @Nullable
    Integer remove(@NonNull String soundId) {
        registrations.remove(soundId);
        return sampleIds.remove(soundId);
    }

    boolean contains(@NonNull String soundId) {
        return registrations.containsKey(soundId);
    }

    @NonNull
    Set<String> soundIds() {
        return Collections.unmodifiableSet(registrations.keySet());
    }

    void clear() {
        registrations.clear();
        sampleIds.clear();
    }
}
