package com.example.core_api.network.model;

public enum TurnEndCause {
    MOVE("MOVE"),
    TIMEOUT("TIMEOUT"),
    UNKNOWN("UNKNOWN");

    public final String name;

    TurnEndCause(String name) {
        this.name = name;
    }
    public static TurnEndCause lookup(String name) {
        switch (name) {
            case "MOVE":
                return MOVE;
            case "TIMEOUT":
                return TIMEOUT;
            default:
                return UNKNOWN;
        }
    }
}
