package com.example.data.datasource.room;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RulesDao {

    @Query("SELECT * FROM rules ORDER BY rule_id ASC")
    @NonNull
    List<RuleEntity> getAll();

    @Query("SELECT * FROM rules WHERE rule_id = :ruleId LIMIT 1")
    RuleEntity findById(int ruleId);

    @Query("SELECT * FROM rules WHERE rule_code = :ruleCode LIMIT 1")
    RuleEntity findByCode(@NonNull String ruleCode);
}
