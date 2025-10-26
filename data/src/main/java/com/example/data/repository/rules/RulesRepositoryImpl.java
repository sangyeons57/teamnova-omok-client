package com.example.data.repository.rules;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.out.rules.RulesRepository;
import com.example.data.datasource.RulesReadableDataSource;
import com.example.data.model.room.RuleDto;
import com.example.data.mapper.RuleMapper;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleId;
import com.example.domain.rules.RuleCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation backed by the local rules database.
 */
public final class RulesRepositoryImpl implements RulesRepository {

    @NonNull
    private final RulesReadableDataSource localDataSource;

    public RulesRepositoryImpl(@NonNull RulesReadableDataSource localDataSource) {
        this.localDataSource = Objects.requireNonNull(localDataSource, "localDataSource == null");
    }

    @NonNull
    @Override
    public List<Rule> loadRules() {
        Log.d("RulesRepositoryImpl", "loadRules: " );
        List<RuleDto> dtos;
        try {
            dtos = localDataSource.getAll();
        } catch (RuntimeException e) {
            Log.e("RulesRepositoryImpl", "loadRules failed to query rules", e);
            throw e;
        }
        Log.d("RulesRepositoryImpl", "loadRules: " + dtos.toString() );
        List<Rule> items = new ArrayList<>(dtos.size());
        Log.d("RulesRepositoryImpl", "loadRules: " + items.toString() );
        for (RuleDto dto : dtos) {
            items.add(RuleMapper.toDomain(dto));
            Log.d("RulesRepositoryImpl", "loadRules: " + dto.toString() );
        }
        return Collections.unmodifiableList(items);
    }

    @Nullable
    @Override
    public Rule findRuleById(@NonNull RuleId ruleId) {
        RuleDto dto = localDataSource.findById(Objects.requireNonNull(ruleId, "ruleId == null").getValue());
        if (dto == null) {
            return null;
        }
        return RuleMapper.toDomain(dto);
    }

    @Nullable
    @Override
    public Rule findRuleByCode(@NonNull RuleCode ruleCode) {
        RuleDto dto = localDataSource.findByCode(Objects.requireNonNull(ruleCode, "ruleCode == null").getValue());
        if (dto == null) {
            return null;
        }
        return RuleMapper.toDomain(dto);
    }
}
