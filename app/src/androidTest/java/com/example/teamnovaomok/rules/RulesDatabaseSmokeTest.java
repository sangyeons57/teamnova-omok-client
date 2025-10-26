package com.example.teamnovaomok.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.core_di.RoomClientContainer;
import com.example.data.datasource.room.RulesDao;
import com.example.data.datasource.room.RuleEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public final class RulesDatabaseSmokeTest {

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("rules.db");
        RoomClientContainer.init(context);
    }

    @Test
    public void databaseFromAsset_containsSeedData() {
        RulesDao dao = RoomClientContainer.getInstance().getDao(RulesDao.class);
        assertNotNull(dao);

        List<RuleEntity> entities = dao.getAll();
        assertFalse(entities.isEmpty());
    }
}
