package com.example.teamnovaomok.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.data.datasource.room.RulesDatabaseConfigFactory;
import com.example.data.datasource.room.RuleEntity;
import com.example.data.datasource.room.RulesRoomDatabase;
import com.example.infra.room.RoomAssetDatabaseProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public final class RulesDatabaseSmokeTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        RoomAssetDatabaseProvider.configure(RulesDatabaseConfigFactory.provideConfig());
        context.deleteDatabase("rules.db");
        RoomAssetDatabaseProvider.initialize(context);
    }

    @Test
    public void databaseFromAsset_containsSeedData() {
        RulesRoomDatabase database = RoomAssetDatabaseProvider.get(RulesRoomDatabase.class);
        assertNotNull(database);

        List<RuleEntity> entities = database.rulesDao().getAll();
        assertFalse(entities.isEmpty());
    }
}
