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
    @ColumnInfo(name = "limit_score", defaultValue = "0")
    private final int limitScore;

    @NonNull
    @ColumnInfo(name = "rule_desc")
    private final String description;

    @NonNull
    @ColumnInfo(name = "created_at")
    private final long createdAt;

    public RuleEntity(int id,
                      @NonNull String code,
                      @NonNull String name,
                      @Nullable String iconPath,
                      @Nullable Integer limitScore,
                      @NonNull String description,
                      Long createdAt) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code == null");
        this.name = Objects.requireNonNull(name, "name == null");
        this.iconPath = iconPath;
        this.limitScore = (limitScore != null) ? limitScore : 0;
        this.description = Objects.requireNonNull(description, "description == null");
        this.createdAt = (createdAt != null) ? createdAt : System.currentTimeMillis();
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

    public int getLimitScore() {
        return limitScore;
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
    public Long getCreatedAt() {
        return createdAt;
    }
}
