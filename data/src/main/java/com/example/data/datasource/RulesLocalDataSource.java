package com.example.data.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.data.datasource.room.RuleEntity;
import com.example.data.datasource.room.RulesDao;
import com.example.data.datasource.room.RulesRoomDatabase;
import com.example.data.model.room.RuleDto;
import com.example.infra.room.RoomAssetDatabaseProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides read-only access to the rules table via Room DAO.
 */
public final class RulesLocalDataSource implements RulesReadableDataSource {

    @NonNull
    private final RulesDao rulesDao;

    public RulesLocalDataSource() {
        RulesRoomDatabase database = RoomAssetDatabaseProvider.get(RulesRoomDatabase.class);
        this.rulesDao = database.rulesDao();
    }

    @Override
    @NonNull
    public List<RuleDto> getAll() {
        List<RuleEntity> entities = rulesDao.getAll();
        List<RuleDto> items = new ArrayList<>(entities.size());
        for (RuleEntity entity : entities) {
            items.add(RuleDto.fromEntity(entity));
        }
        return Collections.unmodifiableList(items);
    }

    @Override
    @Nullable
    public RuleDto findById(int ruleId) {
        RuleEntity entity = rulesDao.findById(ruleId);
        if (entity == null) {
            return null;
        }
        return RuleDto.fromEntity(entity);
    }

    @Override
    @Nullable
    public RuleDto findByCode(@NonNull String ruleCode) {
        String normalizedCode = normalizeKey(Objects.requireNonNull(ruleCode, "ruleCode == null"));
        RuleEntity entity = rulesDao.findByCode(normalizedCode);
        if (entity == null) {
            List<RuleEntity> all = rulesDao.getAll();
            String fallbackKey = normalizeKey(ruleCode);
            for (RuleEntity candidate : all) {
                if (matchesCode(fallbackKey, candidate.getCode())) {
                    entity = candidate;
                    break;
                }
            }
            if (entity == null) {
                return null;
            }
        }
        return RuleDto.fromEntity(entity);
    }

    @NonNull
    private static String normalizeKey(@NonNull String rawCode) {
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

    private static boolean matchesCode(@NonNull String expectedKey, @Nullable String candidateCode) {
        if (candidateCode == null) {
            return false;
        }
        String candidateKey = normalizeKey(candidateCode);
        return candidateKey.equals(expectedKey) || candidateKey.startsWith(expectedKey);
    }
}
