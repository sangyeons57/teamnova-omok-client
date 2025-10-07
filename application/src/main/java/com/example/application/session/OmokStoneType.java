package com.example.application.session;

/**
 * Enumerates the possible stone types that can occupy an Omok board cell.
 */
public enum OmokStoneType {
    BLACK,
    WHITE,
    EMPTY,
    UNKNOWN;

    /**
     * Returns {@code true} if this stone represents an actual piece on the board.
     */
    public boolean isPlaced() {
        return this == BLACK || this == WHITE;
    }
}
