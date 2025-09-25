package com.example.domain.user.entity;

import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserRank;
import com.example.domain.user.value.UserScore;

import java.util.Objects;

public final class RankingEntry {

    private final UserRank rank;
    private final UserId userId;
    private final UserDisplayName displayName;
    private final UserScore score;

    public RankingEntry(UserRank rank, UserId userId, UserDisplayName displayName, UserScore score) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.score = Objects.requireNonNull(score, "score");
    }

    public UserRank getRank() {
        return rank;
    }

    public UserId getUserId() {
        return userId;
    }

    public UserDisplayName getDisplayName() {
        return displayName;
    }

    public UserScore getScore() {
        return score;
    }
}
