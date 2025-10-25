package com.example.data.repository.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.data.datasource.RulesReadableDataSource;
import com.example.data.datasource.room.RuleEntity;
import com.example.data.model.room.RuleDto;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleId;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class RulesRepositoryImplTest {

    private RulesRepositoryImpl repository;

    @Before
    public void setUp() {
        repository = new RulesRepositoryImpl(new FakeRulesDataSource());
    }

    @Test
    public void loadRules_returnsImmutableList() {
        List<Rule> rules = repository.loadRules();
        assertEquals(2, rules.size());
        assertThrows(UnsupportedOperationException.class, () -> rules.add(rules.get(0)));
    }

    @Test
    public void findRuleById_returnsMatch() {
        Rule rule = repository.findRuleById(RuleId.of(1));
        assertNotNull(rule);
        assertEquals("STONE_CONVERSION", rule.getCode().getValue());
    }

    @Test
    public void findRuleById_returnsNullForMissing() {
        Rule rule = repository.findRuleById(RuleId.of(99));
        assertNull(rule);
    }

    private static final class FakeRulesDataSource implements RulesReadableDataSource {

        private final List<RuleDto> rows;

        private FakeRulesDataSource() {
            List<RuleDto> seed = new ArrayList<>();
            seed.add(RuleDto.fromEntity(new RuleEntity(
                    1,
                    "STONE_CONVERSION",
                    "돌 변환",
                    null,
                    "설명",
                    "2025-10-24 07:27:15"
            )));
            seed.add(RuleDto.fromEntity(new RuleEntity(
                    2,
                    "SPEED_GAME",
                    "스피드 게임",
                    null,
                    "스피드 설명",
                    "2025-10-24 07:28:55"
            )));
            rows = Collections.unmodifiableList(seed);
        }

        @NonNull
        @Override
        public List<RuleDto> getAll() {
            return rows;
        }

        @Nullable
        @Override
        public RuleDto findById(int ruleId) {
            for (RuleDto dto : rows) {
                if (dto.getId() == ruleId) {
                    return dto;
                }
            }
            return null;
        }

        @Nullable
        @Override
        public RuleDto findByCode(@NonNull String ruleCode) {
            for (RuleDto dto : rows) {
                if (dto.getCode().equals(ruleCode)) {
                    return dto;
                }
            }
            return null;
        }
    }
}
