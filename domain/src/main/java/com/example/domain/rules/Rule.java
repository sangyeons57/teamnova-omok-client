package com.example.domain.rules;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate representing a game rule.
 */
public final class Rule {

    @NonNull
    private final RuleId id;
    @NonNull
    private final RuleCode code;
    @NonNull
    private final String name;
    @Nullable
    private final String iconPath;
    private final int limitScore;
    @NonNull
    private final String description;
    @NonNull
    private final Instant createdAt;

    private Rule(@NonNull RuleId id,
                 @NonNull RuleCode code,
                 @NonNull String name,
                 @Nullable String iconPath,
                 int limitScore,
                 @NonNull String description,
                 @NonNull Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id == null");
        this.code = Objects.requireNonNull(code, "code == null");
        this.name = Objects.requireNonNull(name, "name == null");
        this.iconPath = iconPath;
        this.limitScore = limitScore;
        this.description = Objects.requireNonNull(description, "description == null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt == null");
    }

    @NonNull
    public static Rule create(@NonNull RuleId id,
                              @NonNull RuleCode code,
                              @NonNull String name,
                              @Nullable String iconPath,
                              int limitScore,
                              @NonNull String description,
                              @NonNull Instant createdAt) {
        return new Rule(id, code, name, iconPath, limitScore, description, createdAt);
    }

    @NonNull
    public RuleId getId() {
        return id;
    }

    @NonNull
    public RuleCode getCode() {
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

    public int getLimitScore() {
        return limitScore;
    }

    @NonNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rule)) {
            return false;
        }
        Rule other = (Rule) obj;
        return id.equals(other.id)
                && code.equals(other.code)
                && name.equals(other.name)
                && Objects.equals(iconPath, other.iconPath)
                && limitScore == other.limitScore
                && description.equals(other.description)
                && createdAt.equals(other.createdAt);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (iconPath != null ? iconPath.hashCode() : 0);
        result = 31 * result + limitScore;
        result = 31 * result + description.hashCode();
        result = 31 * result + createdAt.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Rule{"
                + "id=" + id
                + ", code=" + code
                + ", name='" + name + '\''
                + ", iconPath='" + iconPath + '\''
                + ", limitScore=" + limitScore
                + ", description='" + description + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
