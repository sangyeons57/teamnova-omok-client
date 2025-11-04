package com.example.infra.sound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.sound.SoundManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Coordinates SoundPool loading events and deferred playback requests.
 */
final class SoundSampleLoader {

    private enum LoadState {
        LOADING,
        LOADED,
        FAILED
    }

    private final ConcurrentHashMap<Integer, LoadState> loadStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Queue<PlaybackRequest>> pendingRequests = new ConcurrentHashMap<>();
    private final SoundPlaybackController playbackController;
    private final SoundCallbackDispatcher callbackDispatcher;

    SoundSampleLoader(@NonNull SoundPlaybackController playbackController,
                      @NonNull SoundCallbackDispatcher callbackDispatcher) {
        this.playbackController = playbackController;
        this.callbackDispatcher = callbackDispatcher;
    }

    void markRegistered(int sampleId) {
        loadStates.put(sampleId, LoadState.LOADING);
    }

    void handleSampleReplaced(@Nullable Integer previousSampleId) {
        if (previousSampleId == null) {
            return;
        }
        loadStates.remove(previousSampleId);
        Queue<PlaybackRequest> orphaned = pendingRequests.remove(previousSampleId);
        if (orphaned != null) {
            for (PlaybackRequest request : orphaned) {
                callbackDispatcher.dispatchErrorIfPresent(request.listener, request.soundId,
                        new IllegalStateException("Sound was replaced before load completed: " + request.soundId));
            }
        }
    }

    void handleSampleUnregistered(@Nullable Integer sampleId) {
        if (sampleId == null) {
            return;
        }
        loadStates.remove(sampleId);
        Queue<PlaybackRequest> orphaned = pendingRequests.remove(sampleId);
        if (orphaned != null) {
            for (PlaybackRequest request : orphaned) {
                callbackDispatcher.dispatchErrorIfPresent(request.listener, request.soundId,
                        new IllegalStateException("Sound was unregistered before load completed: " + request.soundId));
            }
        }
    }

    void playOrQueue(@NonNull String soundId,
                     int sampleId,
                     @NonNull SoundManager.SoundRegistration registration,
                     @Nullable SoundManager.PlaybackListener listener) {
        LoadState state = loadStates.get(sampleId);
        if (state == LoadState.LOADED) {
            playbackController.play(soundId, sampleId, registration, listener);
            return;
        }
        if (state == LoadState.FAILED) {
            callbackDispatcher.dispatchErrorIfPresent(listener, soundId,
                    new IllegalStateException("Sound resource failed to load for soundId=" + soundId));
            return;
        }
        pendingRequests.compute(sampleId, (id, queue) -> {
            Queue<PlaybackRequest> result = queue;
            if (result == null) {
                result = new ConcurrentLinkedQueue<>();
            }
            result.add(new PlaybackRequest(soundId, registration, listener));
            return result;
        });
        // Ensure state reflects loading if this is the first time we've seen the sample.
        loadStates.putIfAbsent(sampleId, LoadState.LOADING);
    }

    void onLoadComplete(int sampleId, boolean success) {
        loadStates.put(sampleId, success ? LoadState.LOADED : LoadState.FAILED);
        Queue<PlaybackRequest> queue = pendingRequests.remove(sampleId);
        if (queue == null) {
            return;
        }
        for (PlaybackRequest request : queue) {
            if (success) {
                playbackController.play(request.soundId, sampleId, request.registration, request.listener);
            } else {
                callbackDispatcher.dispatchErrorIfPresent(request.listener, request.soundId,
                        new IllegalStateException("Failed to load sound resource for soundId=" + request.soundId));
            }
        }
    }

    void clear() {
        loadStates.clear();
        Integer[] sampleIds = pendingRequests.keySet().toArray(new Integer[0]);
        for (Integer sampleId : sampleIds) {
            Queue<PlaybackRequest> queue = pendingRequests.remove(sampleId);
            if (queue == null) {
                continue;
            }
            for (PlaybackRequest request : queue) {
                callbackDispatcher.dispatchErrorIfPresent(request.listener, request.soundId,
                        new IllegalStateException("SoundManager released before playback could start for soundId=" + request.soundId));
            }
        }
    }

    private static final class PlaybackRequest {
        private final String soundId;
        private final SoundManager.SoundRegistration registration;
        private final SoundManager.PlaybackListener listener;

        private PlaybackRequest(@NonNull String soundId,
                                @NonNull SoundManager.SoundRegistration registration,
                                @Nullable SoundManager.PlaybackListener listener) {
            this.soundId = soundId;
            this.registration = registration;
            this.listener = listener;
        }
    }
}
