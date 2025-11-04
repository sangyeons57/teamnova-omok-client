package com.example.infra.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import androidx.annotation.NonNull;

/**
 * Thin wrapper around {@link SoundPool} that centralises configuration and exposes
 * convenience operations needed by the sound manager stack.
 */
final class SoundPoolAdapter {

    private static final int MAX_STREAMS = 6;
    private static final int STREAM_PRIORITY = 1;
    private static final float DEFAULT_PLAYBACK_RATE = 1f;

    private final Context appContext;
    private final SoundPool soundPool;

    SoundPoolAdapter(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(attributes)
                .build();
    }

    int load(int rawResId) {
        return soundPool.load(appContext, rawResId, STREAM_PRIORITY);
    }

    void unload(int sampleId) {
        soundPool.unload(sampleId);
    }

    int play(int sampleId,
             float leftVolume,
             float rightVolume,
             boolean looping) {
        int loop = looping ? -1 : 0;
        return soundPool.play(
                sampleId,
                leftVolume,
                rightVolume,
                STREAM_PRIORITY,
                loop,
                DEFAULT_PLAYBACK_RATE
        );
    }

    void stop(int streamId) {
        soundPool.stop(streamId);
    }

    void setOnLoadCompleteListener(@NonNull SoundPool.OnLoadCompleteListener listener) {
        soundPool.setOnLoadCompleteListener(listener);
    }

    void release() {
        soundPool.release();
    }
}
