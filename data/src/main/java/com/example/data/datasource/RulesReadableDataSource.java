package com.example.data.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.data.model.room.RuleDto;

import java.util.List;

/**
 * Read-only contract for obtaining rules.
 */
public interface RulesReadableDataSource {

    @NonNull
    List<RuleDto> getAll();

    @Nullable
    RuleDto findById(int ruleId);

    @Nullable
    RuleDto findByCode(@NonNull String ruleCode);
}
