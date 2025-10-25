package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.example.application.port.out.rules.RulesRepository;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleId;

import java.util.List;
import java.util.Objects;

/**
 * Use case that exposes the rules catalog for presentation layers.
 */
public final class LoadRulesCatalogUseCase {

    private static final String TAG = "LoadRulesCatalogUseCase";

    @NonNull
    private final RulesRepository rulesRepository;

    public LoadRulesCatalogUseCase(@NonNull RulesRepository rulesRepository) {
        this.rulesRepository = Objects.requireNonNull(rulesRepository, "rulesRepository == null");
    }

    @NonNull
    public List<Rule> execute() {
        List<Rule> rules = rulesRepository.loadRules();
        Log.d(TAG, "Loaded rules count=" + rules.size());
        return rules;
    }

    @Nullable
    public Rule findById(@NonNull RuleId ruleId) {
        RuleId safeId = Objects.requireNonNull(ruleId, "ruleId == null");
        Rule rule = rulesRepository.findRuleById(safeId);
        Log.d(TAG, "findById id=" + safeId.getValue() + " -> " + (rule != null ? rule.getCode().getValue() : "null"));
        return rule;
    }

    @Nullable
    public Rule findByCode(@NonNull String ruleCode) {
        Objects.requireNonNull(ruleCode, "ruleCode == null");
        if (ruleCode.trim().isEmpty()) {
            Log.w(TAG, "findByCode skipping empty ruleCode");
            return null;
        }
        Rule rule = rulesRepository.findRuleByCode(com.example.domain.rules.RuleCode.of(ruleCode.trim()));
        Log.d(TAG, "findByCode code=" + ruleCode + " -> " + (rule != null ? rule.getName() : "null"));
        return rule;
    }
}
