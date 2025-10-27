package com.example.application.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.rules.RulesRepository;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleCode;
import com.example.domain.rules.RuleId;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public final class FindRuleByCodeUseCaseTest {

    private FindRuleByCodeUseCase useCase;

    @Before
    public void setUp() {
        useCase = new FindRuleByCodeUseCase(UseCaseConfig.defaultConfig(), new StubRulesRepository());
    }

    @Test
    public void execute_returnsRuleDetails() {
        UResult<Rule> result = useCase.execute("STONE_CONVERSION");
        assertTrue(result instanceof UResult.Ok);
        Rule item = ((UResult.Ok<Rule>) result).value();
        assertEquals("Rule A", item.getName());
        assertEquals("STONE_CONVERSION", item.getCode().getValue());
    }

    @Test
    public void execute_returnsErrorWhenMissing() {
        UResult<Rule> result = useCase.execute("UNKNOWN");
        assertTrue(result instanceof UResult.Err);
        UResult.Err<Rule> err = (UResult.Err<Rule>) result;
        assertEquals("RULE_NOT_FOUND", err.code());
    }

    private static final class StubRulesRepository implements RulesRepository {

        private final List<Rule> rules = Collections.singletonList(
                Rule.create(
                        RuleId.of(1),
                        RuleCode.of("STONE_CONVERSION"),
                        "Rule A",
                        "asset://rules/icons/rule_a.png",
                        500,
                        "Desc",
                        Instant.parse("2025-10-24T07:27:15Z")
                )
        );

        @NonNull
        @Override
        public List<Rule> loadRules() {
            return rules;
        }

        @Nullable
        @Override
        public Rule findRuleById(@NonNull RuleId ruleId) {
            for (Rule rule : rules) {
                if (rule.getId().equals(ruleId)) {
                    return rule;
                }
            }
            return null;
        }

        @Nullable
        @Override
        public Rule findRuleByCode(@NonNull RuleCode ruleCode) {
            for (Rule rule : rules) {
                if (rule.getCode().equals(ruleCode)) {
                    return rule;
                }
            }
            return null;
        }
    }
}
