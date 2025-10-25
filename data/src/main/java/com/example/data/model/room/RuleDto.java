package com.example.data.model.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.data.datasource.room.RuleEntity;

import java.util.Objects;

/**
 * Lightweight transport object for rules fetched from the local database.
 */
public final class RuleDto {

    private final int id;
    @NonNull
    private final String code;
    @NonNull
    private final String name;
    @Nullable
    private final String iconPath;
    @NonNull
    private final String description;
    @NonNull
    private final String createdAt;

    private RuleDto(int id,
                    @NonNull String code,
                    @NonNull String name,
                    @Nullable String iconPath,
                    @NonNull String description,
                    @NonNull String createdAt) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code == null");
        this.name = Objects.requireNonNull(name, "name == null");
        this.iconPath = iconPath;
        this.description = Objects.requireNonNull(description, "description == null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt == null");
    }

    @NonNull
    public static RuleDto fromEntity(@NonNull RuleEntity entity) {
        return new RuleDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getIconPath(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getIconPath() {
        return iconPath;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getCreatedAt() {
        return createdAt;
    }
}
