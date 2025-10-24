package com.example.core_api.sound;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import java.util.Objects;

/**
 * Abstraction for registering and playing short-form audio effects.
 */
public interface SoundManager {

    /**
     * Registers a sound effect that can later be referenced by {@code soundId}.
     */
    void register(@NonNull SoundRegistration registration);

    /**
     * Removes the sound associated with {@code soundId}, stopping any ongoing playback.
     */
    void unregister(@NonNull String soundId);

    /**
     * Returns {@code true} if a sound has been registered for the supplied {@code soundId}.
     */
    boolean isRegistered(@NonNull String soundId);

    /**
     * Plays the sound identified by {@code soundId} using a no-op listener.
     */
    default void play(@NonNull String soundId) {
        play(soundId, null);
    }

    /**
     * Plays the sound identified by {@code soundId}, notifying the optional listener of lifecycle
     * events for the playback session.
     */
    void play(@NonNull String soundId, @Nullable PlaybackListener listener);

    /**
     * Releases all allocated sound resources.
     */
    void release();

    /**
     * Describes a single sound registration.
     */
    final class SoundRegistration {
        private final String soundId;
        private final int rawResId;
        private final float leftVolume;
        private final float rightVolume;
        private final boolean looping;

        public SoundRegistration(@NonNull String soundId, @RawRes int rawResId) {
            this(soundId, rawResId, 1f, 1f, false);
        }

        public SoundRegistration(@NonNull String soundId,
                                 @RawRes int rawResId,
                                 @FloatRange(from = 0.0, to = 1.0) float volume,
                                 boolean looping) {
            this(soundId, rawResId, volume, volume, looping);
        }

        public SoundRegistration(@NonNull String soundId,
                                 @RawRes int rawResId,
                                 @FloatRange(from = 0.0, to = 1.0) float leftVolume,
                                 @FloatRange(from = 0.0, to = 1.0) float rightVolume,
                                 boolean looping) {
            this.soundId = Objects.requireNonNull(soundId, "soundId");
            this.rawResId = rawResId;
            this.leftVolume = clampVolume(leftVolume);
            this.rightVolume = clampVolume(rightVolume);
            this.looping = looping;
        }

        @NonNull
        public String getSoundId() {
            return soundId;
        }

        @RawRes
        public int getRawResId() {
            return rawResId;
        }

        @FloatRange(from = 0.0, to = 1.0)
        public float getLeftVolume() {
            return leftVolume;
        }

        @FloatRange(from = 0.0, to = 1.0)
        public float getRightVolume() {
            return rightVolume;
        }

        public boolean isLooping() {
            return looping;
        }

        private static float clampVolume(float volume) {
            if (Float.isNaN(volume)) {
                return 1f;
            }
            if (volume < 0f) {
                return 0f;
            }
            if (volume > 1f) {
                return 1f;
            }
            return volume;
        }
    }

    /**
     * Listener notified about lifecycle events for a playback request.
     */
    interface PlaybackListener {

        /**
         * Invoked once playback has started.
         */
        default void onStart(@NonNull String soundId) {
        }

        /**
         * Invoked when playback reaches its natural end.
         */
        default void onEnd(@NonNull String soundId) {
        }

        /**
         * Invoked when playback fails before completion.
         */
        default void onError(@NonNull String soundId, @NonNull Exception exception) {
        }
    }
}
