package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.out.rules.RulesRepository;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleId;

import java.util.List;
import java.util.Objects;

/**
 * Use case that exposes the rules catalog for presentation layers.
 */
public final class LoadRulesCatalogUseCase {

    @NonNull
    private final RulesRepository rulesRepository;

    public LoadRulesCatalogUseCase(@NonNull RulesRepository rulesRepository) {
        this.rulesRepository = Objects.requireNonNull(rulesRepository, "rulesRepository == null");
    }

    @NonNull
    public List<Rule> execute() {
        return rulesRepository.loadRules();
    }

    @Nullable
    public Rule findById(@NonNull RuleId ruleId) {
        return rulesRepository.findRuleById(Objects.requireNonNull(ruleId, "ruleId == null"));
    }
}
