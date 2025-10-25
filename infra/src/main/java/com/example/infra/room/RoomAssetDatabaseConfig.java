package com.example.infra.room;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import java.util.Objects;

/**
 * Describes how to instantiate a Room database backed by an asset file.
 */
public final class RoomAssetDatabaseConfig<T extends RoomDatabase> {

    @NonNull
    private final Class<T> databaseClass;
    @NonNull
    private final String databaseName;
    @NonNull
    private final String assetPath;

    public RoomAssetDatabaseConfig(@NonNull Class<T> databaseClass,
                                   @NonNull String databaseName,
                                   @NonNull String assetPath) {
        this.databaseClass = Objects.requireNonNull(databaseClass, "databaseClass == null");
        this.databaseName = Objects.requireNonNull(databaseName, "databaseName == null");
        this.assetPath = Objects.requireNonNull(assetPath, "assetPath == null");
    }

    @NonNull
    public Class<T> getDatabaseClass() {
        return databaseClass;
    }

    @NonNull
    public String getDatabaseName() {
        return databaseName;
    }

    @NonNull
    public String getAssetPath() {
        return assetPath;
    }
}
