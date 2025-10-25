package com.example.data.datasource.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                RuleEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class RulesRoomDatabase extends RoomDatabase {

    public abstract RulesDao rulesDao();
}
