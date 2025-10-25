package com.example.data.datasource.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "rules")
public final class RuleEntity {

    @PrimaryKey
    @ColumnInfo(name = "rule_id")
    private final int id;

    @NonNull
    @ColumnInfo(name = "rule_code")
    private final String code;

    @NonNull
    @ColumnInfo(name = "rule_name")
    private final String name;

    @Nullable
    @ColumnInfo(name = "icon_path")
    private final String iconPath;

    @NonNull
    @ColumnInfo(name = "rule_desc")
    private final String description;

    @NonNull
    @ColumnInfo(name = "created_at")
    private final String createdAt;

    public RuleEntity(int id,
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
