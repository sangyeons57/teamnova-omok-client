package com.example.application.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

public final class ResolveRuleIconSourceUseCaseTest {

    private ResolveRuleIconSourceUseCase useCase;

    @Before
    public void setUp() {
        useCase = new ResolveRuleIconSourceUseCase(UseCaseConfig.defaultConfig(), new StubRulesRepository());
    }

    @Test
    public void execute_returnsAssetSource() {
        UResult<RuleIconSource> result = useCase.execute("STONE_CONVERSION");
        assertTrue(result instanceof UResult.Ok);
        RuleIconSource source = ((UResult.Ok<RuleIconSource>) result).value();
        assertTrue(source.isPresent());
        assertEquals(RuleIconSource.Type.ASSET, source.getType());
        assertEquals("rules/icons/stone.png", source.getValue());
    }

    @Test
    public void execute_returnsDrawableSource() {
        UResult<RuleIconSource> result = useCase.execute("SPEED_GAME");
        assertTrue(result instanceof UResult.Ok);
        RuleIconSource source = ((UResult.Ok<RuleIconSource>) result).value();
        assertTrue(source.isPresent());
        assertEquals(RuleIconSource.Type.DRAWABLE_RESOURCE, source.getType());
        assertEquals("ic_rule_speed_game", source.getValue());
    }

    @Test
    public void execute_returnsNoneWhenMissing() {
        UResult<RuleIconSource> result = useCase.execute("UNKNOWN");
        assertTrue(result instanceof UResult.Ok);
        RuleIconSource source = ((UResult.Ok<RuleIconSource>) result).value();
        assertFalse(source.isPresent());
    }

    private static final class StubRulesRepository implements RulesRepository {

        private final List<Rule> rules = Collections.unmodifiableList(Arrays.asList(
                Rule.create(
                        RuleId.of(1),
                        RuleCode.of("STONE_CONVERSION"),
                        "Rule A",
                        "asset://rules/icons/stone.png",
                        "Desc",
                        Instant.parse("2025-10-24T07:27:15Z")
                ),
                Rule.create(
                        RuleId.of(2),
                        RuleCode.of("SPEED_GAME"),
                        "Rule B",
                        "@drawable/ic_rule_speed_game",
                        "Desc",
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
