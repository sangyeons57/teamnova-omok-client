package com.example.application.session;

/**
 * Represents the selectable game modes offered on the home screen.
 */
public enum GameMode {
    FREE,
    TWO_PLAYER,
    THREE_PLAYER,
    FOUR_PLAYER;

    public static GameMode fromOrdinal(int ordinal) {
        GameMode[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return FREE;
        }
        return values[ordinal];
    }
}
