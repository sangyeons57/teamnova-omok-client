package com.example.application.dto.response;

import com.example.domain.user.entity.RankingEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RankingDataResponse {

    private final List<Entry> entries;

    public RankingDataResponse(List<RankingEntry> rankings) {
        Objects.requireNonNull(rankings, "rankings");
        List<Entry> copy = new ArrayList<>(rankings.size());
        for (RankingEntry entry : rankings) {
            copy.add(new Entry(entry));
        }
        this.entries = Collections.unmodifiableList(copy);
    }

    public List<Entry> entries() {
        return entries;
    }

    public static final class Entry {
        public final int rank;
        public final String userId;
        public final String displayName;
        public final int score;

        private Entry(RankingEntry entry) {
            Objects.requireNonNull(entry, "entry");
            this.rank = entry.rank();
            this.userId = entry.user().getUserId().getValue();
            this.displayName = entry.user().getDisplayName().getValue();
            this.score = entry.user().getScore().getValue();
        }
    }
}

