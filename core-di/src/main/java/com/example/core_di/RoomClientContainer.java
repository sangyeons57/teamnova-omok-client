package com.example.core_di;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.data.datasource.room.RulesDao;
import com.example.data.datasource.room.RulesRoomDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Single-responsibility container that exposes Room DAOs to feature modules.
 * Registration happens internally; consumers only retrieve DAOs that were prepared up-front.
 */
public final class RoomClientContainer {

    private static final String RULES_DB_NAME = "rules.db";
    private static final String RULES_DB_ASSET_PATH = "databases/rules.db";
    private static final String TAG = "RoomClientContainer";

    private static volatile RoomClientContainer instance;

    public static void init(@NonNull Context context) {
        Objects.requireNonNull(context, "context == null");
        synchronized (RoomClientContainer.class) {
            RoomClientContainer previous = instance;
            if (previous != null) {
                instance = null;
                previous.shutdownInternal();
            }
            RulesRoomDatabase database = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RulesRoomDatabase.class,
                            RULES_DB_NAME
                    )
                    .createFromAsset(RULES_DB_ASSET_PATH)
                    .fallbackToDestructiveMigration(true)
                    .build();
            instance = new RoomClientContainer(database);
        }
    }

    public static RoomClientContainer getInstance() {
        RoomClientContainer container = instance;
        if (container == null) {
            throw new IllegalStateException("RoomClientContainer not initialised");
        }
        return container;
    }

    private final RulesRoomDatabase rulesDatabase;
    private final Map<Class<?>, Object> daoRegistry = new HashMap<>();

    private RoomClientContainer(@NonNull RulesRoomDatabase rulesDatabase) {
        this.rulesDatabase = Objects.requireNonNull(rulesDatabase, "rulesDatabase == null");
        registerDefaults();
        verifySchema();
    }

    private void registerDefaults() {
        daoRegistry.put(RulesDao.class, rulesDatabase.rulesDao());
    }

    private void verifySchema() {
        try {
            SupportSQLiteDatabase db = rulesDatabase.getOpenHelper().getReadableDatabase();
            try (Cursor cursor = db.query("PRAGMA table_info(rules)")) {
                StringBuilder columns = new StringBuilder("rules table columns:");
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    int notNull = cursor.getInt(cursor.getColumnIndexOrThrow("notnull"));
                    columns.append("\n - ")
                            .append(name)
                            .append(" (")
                            .append(type)
                            .append(") notNull=")
                            .append(notNull);
                }
                Log.d(TAG, columns.toString());
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to verify rules schema", e);
        }
    }

    public boolean isReady() {
        return !daoRegistry.isEmpty();
    }

    @NonNull
    public <T> T getDao(@NonNull Class<T> daoClass) {
        Objects.requireNonNull(daoClass, "daoClass == null");
        Object dao = daoRegistry.get(daoClass);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO registered for " + daoClass.getName());
        }
        return daoClass.cast(dao);
    }

    private void shutdownInternal() {
        if (rulesDatabase.isOpen()) {
            rulesDatabase.close();
        }
        daoRegistry.clear();
    }

    void clearForTest() {
        synchronized (RoomClientContainer.class) {
            shutdownInternal();
            instance = null;
        }
    }
}
