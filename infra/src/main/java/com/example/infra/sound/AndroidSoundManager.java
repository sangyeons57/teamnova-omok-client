package com.example.infra.sound;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.sound.SoundManager;

import java.util.Objects;

/**
 * Orchestrates sound registration, loading, playback and callback dispatching.
 */
public final class AndroidSoundManager implements SoundManager {

    private final Context appContext;
    private final SoundPoolAdapter soundPoolAdapter;
    private final SoundCallbackDispatcher callbackDispatcher;
    private final SoundPlaybackController playbackController;
    private final SoundRegistry registry;
    private final SoundSampleLoader sampleLoader;
    private volatile boolean released;

    public AndroidSoundManager(@NonNull Context context) {
        this.appContext = Objects.requireNonNull(context, "context").getApplicationContext();
        this.soundPoolAdapter = new SoundPoolAdapter(appContext);
        this.callbackDispatcher = new SoundCallbackDispatcher();
        this.playbackController = new SoundPlaybackController(soundPoolAdapter, callbackDispatcher);
        this.registry = new SoundRegistry();
        this.sampleLoader = new SoundSampleLoader(playbackController, callbackDispatcher);
        soundPoolAdapter.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (released) {
                return;
            }
            callbackDispatcher.getMainHandler().post(() ->
                    sampleLoader.onLoadComplete(sampleId, status == 0));
        });
    }

    @Override
    public void register(@NonNull SoundRegistration registration) {
        Objects.requireNonNull(registration, "registration");
        if (released) {
            return;
        }
        String soundId = registration.getSoundId();
        int sampleId = soundPoolAdapter.load(registration.getRawResId());
        Integer previousSample = registry.register(registration, sampleId);
        sampleLoader.handleSampleReplaced(previousSample);
        sampleLoader.markRegistered(sampleId);
        playbackController.stop(soundId);
        if (previousSample != null) {
            soundPoolAdapter.unload(previousSample);
        }
    }

    @Override
    public void unregister(@NonNull String soundId) {
        Objects.requireNonNull(soundId, "soundId");
        if (released) {
            return;
        }
        Integer sampleId = registry.remove(soundId);
        playbackController.stop(soundId);
        if (sampleId != null) {
            sampleLoader.handleSampleUnregistered(sampleId);
            soundPoolAdapter.unload(sampleId);
        }
    }

    @Override
    public boolean isRegistered(@NonNull String soundId) {
        Objects.requireNonNull(soundId, "soundId");
        return registry.contains(soundId);
    }

    @Override
    public void play(@NonNull String soundId, @Nullable PlaybackListener listener) {
        Objects.requireNonNull(soundId, "soundId");
        if (released) {
            callbackDispatcher.dispatchErrorIfPresent(listener, soundId,
                    new IllegalStateException("SoundManager has been released."));
            return;
        }
        SoundRegistration registration = registry.getRegistration(soundId);
        if (registration == null) {
            callbackDispatcher.dispatchErrorIfPresent(listener, soundId,
                    new IllegalStateException("Sound not registered: " + soundId));
            return;
        }
        Integer sampleId = registry.getSampleId(soundId);
        if (sampleId == null) {
            callbackDispatcher.dispatchErrorIfPresent(listener, soundId,
                    new IllegalStateException("Sample not loaded for soundId=" + soundId));
            return;
        }
        Runnable command = () -> sampleLoader.playOrQueue(soundId, sampleId, registration, listener);
        if (Looper.myLooper() == callbackDispatcher.getMainHandler().getLooper()) {
            command.run();
        } else {
            callbackDispatcher.getMainHandler().post(command);
        }
    }

    @Override
    public void release() {
        if (released) {
            return;
        }
        released = true;
        playbackController.stopAll();
        sampleLoader.clear();
        registry.clear();
        soundPoolAdapter.release();
    }
}
