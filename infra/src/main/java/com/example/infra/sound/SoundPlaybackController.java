package com.example.infra.sound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.sound.SoundManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orchestrates playback and ensures that only one stream per sound identifier is active at once.
 */
final class SoundPlaybackController {

    private final SoundPoolAdapter soundPoolAdapter;
    private final SoundCallbackDispatcher callbackDispatcher;
    private final Map<String, Integer> activeStreams = new ConcurrentHashMap<>();
    private final Map<String, SoundManager.PlaybackListener> activeListeners = new ConcurrentHashMap<>();

    SoundPlaybackController(@NonNull SoundPoolAdapter soundPoolAdapter,
                            @NonNull SoundCallbackDispatcher callbackDispatcher) {
        this.soundPoolAdapter = soundPoolAdapter;
        this.callbackDispatcher = callbackDispatcher;
    }

    void play(@NonNull String soundId,
              int sampleId,
              @NonNull SoundManager.SoundRegistration registration,
              @Nullable SoundManager.PlaybackListener listener) {
        stop(soundId);
        int streamId = soundPoolAdapter.play(
                sampleId,
                registration.getLeftVolume(),
                registration.getRightVolume(),
                registration.isLooping()
        );
        if (streamId == 0) {
            callbackDispatcher.dispatchErrorIfPresent(listener, soundId,
                    new IllegalStateException("Failed to start SoundPool playback for soundId=" + soundId));
            return;
        }
        activeStreams.put(soundId, streamId);
        if (listener != null) {
            activeListeners.put(soundId, listener);
        } else {
            activeListeners.remove(soundId);
        }
        callbackDispatcher.dispatchStartIfPresent(listener, soundId);
    }

    void stop(@NonNull String soundId) {
        Integer streamId = activeStreams.remove(soundId);
        SoundManager.PlaybackListener previousListener = activeListeners.remove(soundId);
        if (streamId != null) {
            soundPoolAdapter.stop(streamId);
        }
        callbackDispatcher.dispatchEndIfPresent(previousListener, soundId);
    }

    void stopAll() {
        for (String soundId : activeStreams.keySet()) {
            stop(soundId);
        }
    }
}
