package com.example.application.session;

/**
 * Represents the selectable game modes offered on the home screen.
 */
public enum GameMode {
    FREE(1),
    TWO_PLAYER(2),
    THREE_PLAYER(3),
    FOUR_PLAYER(4);

    private final int code;

    private GameMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GameMode fromOrdinal(int ordinal) {
        GameMode[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return FREE;
        }
        return values[ordinal];
    }
}
