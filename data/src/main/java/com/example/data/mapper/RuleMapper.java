package com.example.data.mapper;

import androidx.annotation.NonNull;

import com.example.data.model.room.RuleDto;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleCode;
import com.example.domain.rules.RuleId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Maps persistence DTOs into domain entities.
 */
public final class RuleMapper {

    private static final DateTimeFormatter SQLITE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RuleMapper() {
        // No instances.
    }

    @NonNull
    public static Rule toDomain(@NonNull RuleDto dto) {
        return Rule.create(
                RuleId.of(dto.getId()),
                RuleCode.of(dto.getCode()),
                dto.getName(),
                dto.getIconPath(),
                dto.getDescription(),
                parseInstant(dto.getCreatedAt())
        );
    }

    @NonNull
    private static Instant parseInstant(@NonNull String createdAt) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(createdAt, SQLITE_TIMESTAMP);
            return localDateTime.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
            return Instant.EPOCH;
        }
    }
}
