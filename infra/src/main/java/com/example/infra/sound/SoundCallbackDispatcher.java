package com.example.infra.sound;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.sound.SoundManager;

/**
 * Ensures that {@link SoundManager.PlaybackListener} callbacks are dispatched on the main thread.
 */
final class SoundCallbackDispatcher {

    private final Handler mainHandler;

    SoundCallbackDispatcher() {
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    void dispatchStartIfPresent(@Nullable SoundManager.PlaybackListener listener, @NonNull String soundId) {
        if (listener != null) {
            dispatchStart(listener, soundId);
        }
    }

    void dispatchEndIfPresent(@Nullable SoundManager.PlaybackListener listener, @NonNull String soundId) {
        if (listener != null) {
            dispatchEnd(listener, soundId);
        }
    }

    void dispatchErrorIfPresent(@Nullable SoundManager.PlaybackListener listener,
                                @NonNull String soundId,
                                @NonNull Exception exception) {
        if (listener != null) {
            dispatchError(listener, soundId, exception);
        }
    }

    @NonNull
    Handler getMainHandler() {
        return mainHandler;
    }

    private void postIfNeeded(@NonNull Runnable runnable) {
        if (Looper.myLooper() == mainHandler.getLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }

    private void dispatchStart(@NonNull SoundManager.PlaybackListener listener, @NonNull String soundId) {
        postIfNeeded(() -> listener.onStart(soundId));
    }

    private void dispatchEnd(@NonNull SoundManager.PlaybackListener listener, @NonNull String soundId) {
        postIfNeeded(() -> listener.onEnd(soundId));
    }

    private void dispatchError(@NonNull SoundManager.PlaybackListener listener,
                               @NonNull String soundId,
                               @NonNull Exception exception) {
        postIfNeeded(() -> listener.onError(soundId, exception));
    }
}
