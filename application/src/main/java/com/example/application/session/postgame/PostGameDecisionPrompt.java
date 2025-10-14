package com.example.application.session.postgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.out.realtime.PostGameDecisionOption;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the server-issued prompt asking players for a post-game decision.
 */
public final class PostGameDecisionPrompt {

    private final String sessionId;
    private final long deadlineAt;
    private final List<PostGameDecisionOption> options;
    private final PostGameDecisionOption autoAction;

    public PostGameDecisionPrompt(@NonNull String sessionId,
                                  long deadlineAt,
                                  @NonNull List<PostGameDecisionOption> options,
                                  @Nullable PostGameDecisionOption autoAction) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.deadlineAt = deadlineAt;
        this.options = List.copyOf(Objects.requireNonNull(options, "options"));
        this.autoAction = autoAction != null ? autoAction : PostGameDecisionOption.UNKNOWN;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    public long getDeadlineAt() {
        return deadlineAt;
    }

    @NonNull
    public List<PostGameDecisionOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    @NonNull
    public PostGameDecisionOption getAutoAction() {
        return autoAction;
    }
}
