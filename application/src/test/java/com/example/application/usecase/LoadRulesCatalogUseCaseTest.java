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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class LoadRulesCatalogUseCaseTest {

    private LoadRulesCatalogUseCase useCase;

    @Before
    public void setUp() {
        useCase = new LoadRulesCatalogUseCase(UseCaseConfig.defaultConfig(), new FakeRulesRepository());
    }

    @Test
    public void execute_returnsCatalogItems() {
        UResult<List<Rule>> result = useCase.loadCatalog();
        assertTrue(result instanceof UResult.Ok);
        @SuppressWarnings("unchecked")
        UResult.Ok<List<Rule>> ok = (UResult.Ok<List<Rule>>) result;
        List<Rule> items = ok.value();
        assertEquals(2, items.size());
        Rule first = items.get(0);
        assertEquals("STONE_CONVERSION", first.getCode().getValue());
        assertEquals("Rule A", first.getName());
    }

    private static final class FakeRulesRepository implements RulesRepository {

        private final List<Rule> rules = Collections.unmodifiableList(Arrays.asList(
                Rule.create(
                        RuleId.of(1),
                        RuleCode.of("STONE_CONVERSION"),
                        "Rule A",
                        null,
                        1500,
                        "Desc A",
                        Instant.parse("2025-10-24T07:27:15Z")
                ),
                Rule.create(
                        RuleId.of(2),
                        RuleCode.of("SPEED_GAME"),
                        "Rule B",
                        null,
                        2000,
                        "Desc B",
                        Instant.parse("2025-10-24T07:28:55Z")
                )
        ));

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
