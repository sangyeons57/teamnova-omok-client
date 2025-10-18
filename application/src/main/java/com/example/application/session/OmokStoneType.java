package com.example.application.session;

/**
 * Enumerates the possible stone types that can occupy an Omok board cell.
 */
public enum OmokStoneType {
    BLACK(5),
    WHITE(4),
    RED(0),
    BLUE(1),
    YELLOW(2),
    GREEN(3),
    JOKER(4),
    BLOCKER(5),
    EMPTY(-1),
    UNKNOWN(-1);

    public final int index;
    OmokStoneType(int index) {
        this.index = index;
    }

    /**
     * Returns {@code true} if this stone represents an actual piece on the board.
     */
    public boolean isPlaced() {
        return this.index >= 0;
    }
}
