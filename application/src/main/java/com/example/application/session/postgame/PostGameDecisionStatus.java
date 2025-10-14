package com.example.application.session.postgame;

import androidx.annotation.NonNull;

import com.example.application.port.out.realtime.PostGameDecisionOption;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Aggregates per-player decisions and remaining participants.
 */
public final class PostGameDecisionStatus {

    private final String sessionId;
    private final Map<String, PostGameDecisionOption> decisions;
    private final List<String> remainingUserIds;

    public PostGameDecisionStatus(@NonNull String sessionId,
                                  @NonNull Map<String, PostGameDecisionOption> decisions,
                                  @NonNull List<String> remainingUserIds) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        Map<String, PostGameDecisionOption> copy = new LinkedHashMap<>();
        for (Map.Entry<String, PostGameDecisionOption> entry : Objects.requireNonNull(decisions, "decisions").entrySet()) {
            String key = Objects.requireNonNull(entry.getKey(), "decision userId");
            PostGameDecisionOption option = entry.getValue() != null
                    ? entry.getValue()
                    : PostGameDecisionOption.UNKNOWN;
            copy.put(key, option);
        }
        this.decisions = Collections.unmodifiableMap(copy);
        this.remainingUserIds = List.copyOf(Objects.requireNonNull(remainingUserIds, "remainingUserIds"));
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    public Map<String, PostGameDecisionOption> getDecisions() {
        return decisions;
    }

    @NonNull
    public List<String> getRemainingUserIds() {
        return remainingUserIds;
    }
}
