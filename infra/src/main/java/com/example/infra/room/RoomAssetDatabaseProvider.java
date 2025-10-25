package com.example.infra.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.Objects;

/**
 * Lazily instantiates a Room database using a bundled asset.
 */
public final class RoomAssetDatabaseProvider {

    private static final Object LOCK = new Object();

    @Nullable
    private static RoomDatabase instance;
    @Nullable
    private static RoomAssetDatabaseConfig<? extends RoomDatabase> config;

    private RoomAssetDatabaseProvider() {
        // No instances.
    }

    public static <T extends RoomDatabase> void configure(@NonNull RoomAssetDatabaseConfig<T> databaseConfig) {
        synchronized (LOCK) {
            if (instance != null) {
                instance.close();
                instance = null;
            }
            config = Objects.requireNonNull(databaseConfig, "databaseConfig == null");
        }
    }

    public static void initialize(@NonNull Context context) {
        Objects.requireNonNull(context, "context == null");
        synchronized (LOCK) {
            if (config == null) {
                throw new IllegalStateException("RoomAssetDatabaseProvider config not set");
            }
            if (instance == null) {
                Context appContext = context.getApplicationContext();
                instance = Room.databaseBuilder(
                                appContext,
                                config.getDatabaseClass(),
                                config.getDatabaseName()
                        )
                        .createFromAsset(config.getAssetPath())
                        .build();
            }
        }
    }

    @NonNull
    public static <T extends RoomDatabase> T get(@NonNull Class<T> databaseClass) {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("RoomAssetDatabaseProvider not initialised");
            }
            return Objects.requireNonNull(databaseClass.cast(instance));
        }
    }
}
