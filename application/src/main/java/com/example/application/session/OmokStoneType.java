package com.example.application.session;

/**
 * Enumerates the possible stone types that can occupy an Omok board cell.
 */
public enum OmokStoneType {
    BLACK,
    WHITE,
    RED,
    BLUE,
    YELLOW,
    GREEN,
    JOKER,
    BLOCKER,
    EMPTY,
    UNKNOWN;

    /**
     * Returns {@code true} if this stone represents an actual piece on the board.
     */
    public boolean isPlaced() {
        return switch (this) {
            case RED, BLUE, YELLOW, GREEN, JOKER, BLOCKER, BLACK, WHITE -> true;
            case EMPTY, UNKNOWN -> false;
        };
    }
}
