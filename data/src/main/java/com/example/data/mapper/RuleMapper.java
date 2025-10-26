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
        String normalizedCode = normalizeCode(dto.getCode());
        String normalizedIcon = dto.getIconPath();
        if (normalizedIcon != null) {
            normalizedIcon = normalizedIcon.trim();
            if (normalizedIcon.isEmpty()) {
                normalizedIcon = null;
            }
        }
        return Rule.create(
                RuleId.of(dto.getId()),
                RuleCode.of(normalizedCode),
                dto.getName(),
                normalizedIcon,
                dto.getDescription(),
                parseInstant(dto.getCreatedAt())
        );
    }

    @NonNull
    private static String normalizeCode(@NonNull String rawCode) {
        String trimmed = rawCode.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        String replaced = trimmed.replace('-', '_').replace(' ', '_');
        StringBuilder buffer = new StringBuilder(replaced.length());
        for (int i = 0; i < replaced.length(); i++) {
            char ch = replaced.charAt(i);
            if (ch == '_') {
                buffer.append('_');
                continue;
            }
            if (Character.isLetterOrDigit(ch)) {
                buffer.append(Character.toUpperCase(ch));
            }
        }
        return buffer.toString();
    }

    @NonNull
    private static Instant parseInstant(@NonNull Long createdAt) {
        try {
            return Instant.ofEpochMilli(createdAt);
        } catch (DateTimeParseException ignored) {
            return Instant.EPOCH;
        }
    }
}
