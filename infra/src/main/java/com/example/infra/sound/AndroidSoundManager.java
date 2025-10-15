package com.example.infra.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core.sound.SoundManager;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Android-backed {@link SoundManager} implementation that drives playback through {@link MediaPlayer}.
 */
public final class AndroidSoundManager implements SoundManager {

    private final Context appContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Map<String, SoundRegistration> registry = new ConcurrentHashMap<>();
    private final Map<String, Set<MediaPlayer>> activePlayers = new ConcurrentHashMap<>();

    public AndroidSoundManager(@NonNull Context context) {
        this.appContext = Objects.requireNonNull(context, "context").getApplicationContext();
    }

    @Override
    public void register(@NonNull SoundRegistration registration) {
        Objects.requireNonNull(registration, "registration");
        registry.put(registration.getSoundId(), registration);
    }

    @Override
    public void unregister(@NonNull String soundId) {
        Objects.requireNonNull(soundId, "soundId");
        registry.remove(soundId);
        stopActivePlayers(soundId);
    }

    @Override
    public boolean isRegistered(@NonNull String soundId) {
        Objects.requireNonNull(soundId, "soundId");
        return registry.containsKey(soundId);
    }

    @Override
    public void play(@NonNull String soundId, @Nullable PlaybackListener listener) {
        Objects.requireNonNull(soundId, "soundId");
        SoundRegistration registration = registry.get(soundId);
        if (registration == null) {
            notifyError(soundId, listener, new IllegalStateException("Sound not registered: " + soundId));
            return;
        }
        Runnable command = () -> startPlayback(soundId, registration, listener);
        if (Looper.myLooper() == mainHandler.getLooper()) {
            command.run();
        } else {
            mainHandler.post(command);
        }
    }

    @Override
    public void release() {
        for (String soundId : registry.keySet()) {
            stopActivePlayers(soundId);
        }
        registry.clear();
    }

    private void startPlayback(@NonNull String soundId,
                               @NonNull SoundRegistration registration,
                               @Nullable PlaybackListener listener) {
        MediaPlayer player = MediaPlayer.create(appContext, registration.getRawResId());
        if (player == null) {
            notifyError(soundId, listener,
                    new IllegalStateException("Unable to create MediaPlayer for soundId=" + soundId));
            return;
        }
        player.setLooping(registration.isLooping());
        player.setVolume(registration.getLeftVolume(), registration.getRightVolume());
        player.setOnCompletionListener(mp -> {
            removeActivePlayer(soundId, mp);
            mp.release();
            notifyEnd(soundId, listener);
        });
        player.setOnErrorListener((mp, what, extra) -> {
            removeActivePlayer(soundId, mp);
            mp.release();
            notifyError(soundId, listener,
                    new IllegalStateException("MediaPlayer error for soundId="
                            + soundId + " what=" + what + " extra=" + extra));
            return true;
        });
        addActivePlayer(soundId, player);
        notifyStart(soundId, listener);
        try {
            player.start();
        } catch (IllegalStateException ex) {
            removeActivePlayer(soundId, player);
            player.release();
            notifyError(soundId, listener, ex);
        }
    }

    private void stopActivePlayers(@NonNull String soundId) {
        Set<MediaPlayer> players = activePlayers.remove(soundId);
        if (players == null) {
            return;
        }
        for (MediaPlayer player : players) {
            try {
                player.setOnCompletionListener(null);
                player.setOnErrorListener(null);
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (IllegalStateException ignored) {
                // Player might already be released, ignore.
            } finally {
                player.release();
            }
        }
    }

    private void addActivePlayer(@NonNull String soundId, @NonNull MediaPlayer player) {
        activePlayers.compute(soundId, (key, current) -> {
            Set<MediaPlayer> players = current;
            if (players == null) {
                players = Collections.newSetFromMap(new ConcurrentHashMap<>());
            }
            players.add(player);
            return players;
        });
    }

    private void removeActivePlayer(@NonNull String soundId, @NonNull MediaPlayer player) {
        Set<MediaPlayer> players = activePlayers.get(soundId);
        if (players == null) {
            return;
        }
        players.remove(player);
        if (players.isEmpty()) {
            activePlayers.remove(soundId);
        }
    }

    private void notifyStart(@NonNull String soundId, @Nullable PlaybackListener listener) {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == mainHandler.getLooper()) {
            listener.onStart(soundId);
        } else {
            mainHandler.post(() -> listener.onStart(soundId));
        }
    }

    private void notifyEnd(@NonNull String soundId, @Nullable PlaybackListener listener) {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == mainHandler.getLooper()) {
            listener.onEnd(soundId);
        } else {
            mainHandler.post(() -> listener.onEnd(soundId));
        }
    }

    private void notifyError(@NonNull String soundId,
                             @Nullable PlaybackListener listener,
                             @NonNull Exception exception) {
        if (listener == null) {
            return;
        }
        if (Looper.myLooper() == mainHandler.getLooper()) {
            listener.onError(soundId, exception);
        } else {
            mainHandler.post(() -> listener.onError(soundId, exception));
        }
    }
}
