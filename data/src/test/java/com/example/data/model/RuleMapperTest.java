package com.example.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.data.datasource.room.RuleEntity;
import com.example.data.model.room.RuleDto;
import com.example.data.mapper.RuleMapper;
import com.example.domain.rules.Rule;

import org.junit.Test;

import java.time.Instant;

public final class RuleMapperTest {

    @Test
    public void toDomain_parsesValidTimestamp() {
        RuleEntity entity = new RuleEntity(
                1,
                "ALPHA",
                "Alpha Rule",
                null,
                "Description",
                "2025-10-24 07:27:15"
        );
        RuleDto dto = RuleDto.fromEntity(entity);

        Rule rule = RuleMapper.toDomain(dto);

        assertEquals(1, rule.getId().getValue());
        assertEquals("ALPHA", rule.getCode().getValue());
        assertEquals("Alpha Rule", rule.getName());
        assertEquals("Description", rule.getDescription());
        assertEquals(Instant.parse("2025-10-24T07:27:15Z"), rule.getCreatedAt());
    }

    @Test
    public void toDomain_handlesMalformedTimestamp() {
        RuleEntity entity = new RuleEntity(
                2,
                "BETA",
                "Beta",
                null,
                "Desc",
                "not-a-date"
        );
        RuleDto dto = RuleDto.fromEntity(entity);

        Rule rule = RuleMapper.toDomain(dto);

        assertNotNull(rule);
        assertEquals(Instant.EPOCH, rule.getCreatedAt());
    }
}
