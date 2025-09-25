package com.example.domain.user.entity;

import java.util.Objects;

/**
 * Represents a single ranking slot with the associated user profile.
 */
public record RankingEntry(int rank, User user) {

    public RankingEntry {
        if (rank <= 0) {
            throw new IllegalArgumentException("rank must be positive");
        }
        Objects.requireNonNull(user, "user");
    }
}

