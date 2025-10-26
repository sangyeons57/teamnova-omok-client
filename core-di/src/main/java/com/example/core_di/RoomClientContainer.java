package com.example.core_di;

import androidx.annotation.NonNull;

import com.example.data.datasource.room.RulesDao;
import com.example.data.datasource.room.RulesRoomDatabase;
import com.example.infra.room.RoomAssetDatabaseProvider;

import java.util.Objects;

/**
 * Provides access to Room-backed DAOs required by data sources.
 */
public final class RoomClientContainer {

    private static RoomClientContainer instance;

    public static RoomClientContainer getInstance() {
        if (instance == null) {
            instance = new RoomClientContainer();
        }
        return instance;
    }

    private volatile RulesDao rulesDao;

    private RoomClientContainer() {
        // No instances.
    }

    public void init(@NonNull RulesRoomDatabase database) {
        Objects.requireNonNull(database, "database == null");
        if (rulesDao != null) {
            return;
        }
        synchronized (this) {
            if (rulesDao == null) rulesDao = database.rulesDao();
        }
    }

    @NonNull
    public RulesDao get() {
        RulesDao dao = rulesDao;
        if (dao == null) {
            synchronized (this) {
                if (rulesDao == null) {
                    RulesRoomDatabase database = RoomAssetDatabaseProvider.get(RulesRoomDatabase.class);
                    rulesDao = database.rulesDao();
                }
                dao = rulesDao;
            }
        }
        if (dao == null) {
            throw new IllegalStateException("RoomClientContainer not initialised");
        }
        return dao;
    }

    void clearForTest() {
        synchronized (this) {
            rulesDao = null;
        }
    }
}
