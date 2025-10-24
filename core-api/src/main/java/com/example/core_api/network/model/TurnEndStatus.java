package com.example.core_api.network.model;

public enum TurnEndStatus {
    UNKNOWN("UNKNOWN"),

    TIMEOUT("TIMEOUT"),

    SUCCESS("SUCCESS"),
    INVALID_PLAYER("INVALID_PLAYER"),
    INVALID_TURN("INVALID_TURN"),
    GAME_NOT_STARTED("GAME_NOT_STARTED"),
    GAME_NOT_FOUND("GAME_NOT_FOUND"),
    OUT_OF_TURN("OUT_OF_TURN"),
    OUT_OF_BOUNDS("OUT_OF_BOUNDS"),
    INVALID_MOVE("INVALID_MOVE"),
    CELL_OCCUPIED("CELL_OCCUPIED"),
    GAME_FINISHED("GAME_FINISHED"),
    RESTRICTED_ZONE("RESTRICTED_ZONE");

    public final String name;

    TurnEndStatus(String name) {
        this.name = name;
    }
    public static TurnEndStatus lookup(String name) {
        switch (name) {
            case "TIMEOUT":
                return TIMEOUT;
            case "SUCCESS":
                return SUCCESS;
            case "INVALID_PLAYER":
                return INVALID_PLAYER;
            case "INVALID_TURN":
                return INVALID_TURN;
            case "GAME_NOT_STARTED":
                return GAME_NOT_STARTED;
            case "GAME_NOT_FOUND":
                return GAME_NOT_FOUND;
            case "OUT_OF_TURN":
                return OUT_OF_TURN;
            case "OUT_OF_BOUNDS":
                return OUT_OF_BOUNDS;
            case "INVALID_MOVE":
                return INVALID_MOVE;
            case "CELL_OCCUPIED":
                return CELL_OCCUPIED;
            case "GAME_FINISHED":
                return GAME_FINISHED;
            case "RESTRICTED_ZONE":
                return RESTRICTED_ZONE;
            default:
                return UNKNOWN;
        }
    }
}
