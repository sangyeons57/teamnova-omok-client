package com.example.application.session;

public enum MatchState {
    /**
     * No matching in progress.
     */
    IDLE,
    /**
     * Matching is in progress.
     */
    MATCHING,
    /**
     * A match has been found.
     */
    MATCHED
}
