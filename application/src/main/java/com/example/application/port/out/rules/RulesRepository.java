package com.example.application.port.out.rules;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleId;
import com.example.domain.rules.RuleCode;

import java.util.List;

/**
 * Outbound port exposing access to rules.
 */
public interface RulesRepository {

    @NonNull
    List<Rule> loadRules();

    @Nullable
    Rule findRuleById(@NonNull RuleId ruleId);

    @Nullable
    Rule findRuleByCode(@NonNull RuleCode ruleCode);
}
