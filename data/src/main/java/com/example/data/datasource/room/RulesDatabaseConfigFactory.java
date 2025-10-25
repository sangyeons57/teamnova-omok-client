package com.example.data.datasource.room;

import androidx.annotation.NonNull;

import com.example.infra.room.RoomAssetDatabaseConfig;

/**
 * Centralises configuration for the bundled rules database asset.
 */
public final class RulesDatabaseConfigFactory {

    private static final RoomAssetDatabaseConfig<RulesRoomDatabase> CONFIG =
            new RoomAssetDatabaseConfig<>(
                    RulesRoomDatabase.class,
                    "rules.db",
                    "databases/rules.db"
            );

    private RulesDatabaseConfigFactory() {
        // No instances.
    }

    @NonNull
    public static RoomAssetDatabaseConfig<RulesRoomDatabase> provideConfig() {
        return CONFIG;
    }
}
