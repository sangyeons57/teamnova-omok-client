package com.example.core_di;

import androidx.annotation.NonNull;

import com.example.data.datasource.RulesLocalDataSource;
import com.example.data.datasource.RulesReadableDataSource;
import com.example.data.datasource.room.RulesDao;
import com.example.data.datasource.room.RulesRoomDatabase;
import com.example.infra.room.RoomAssetDatabaseProvider;

/**
 * Lazily provides access to the local rules data source.
 */
public final class RulesDataSourceContainer {

    private static volatile RulesReadableDataSource localDataSource;

    private RulesDataSourceContainer() {
        // No instances.
    }

    public static void init() {
        if (localDataSource != null) {
            return;
        }
        synchronized (RulesDataSourceContainer.class) {
            if (localDataSource == null) {
                localDataSource = new RulesLocalDataSource();
            }
        }
    }

    @NonNull
    public static RulesReadableDataSource getLocalDataSource() {
        RulesReadableDataSource dataSource = localDataSource;
        if (dataSource == null) {
            throw new IllegalStateException("RulesDataSourceContainer not initialised");
        }
        return dataSource;
    }
}
