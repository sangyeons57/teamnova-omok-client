package com.example.data.repository.rules;

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
        List<RuleDto> dtos = localDataSource.getAll();
        List<Rule> items = new ArrayList<>(dtos.size());
        for (RuleDto dto : dtos) {
            items.add(RuleMapper.toDomain(dto));
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
