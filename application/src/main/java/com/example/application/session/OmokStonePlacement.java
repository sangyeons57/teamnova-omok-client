package com.example.application.session;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Describes a single stone placement on the Omok board.
 */
public final class OmokStonePlacement {

    private final int x;
    private final int y;
    private final OmokStoneType stoneType;

    public OmokStonePlacement(int x, int y, @NonNull OmokStoneType stoneType) {
        this.x = x;
        this.y = y;
        this.stoneType = Objects.requireNonNull(stoneType, "stoneType");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @NonNull
    public OmokStoneType getStoneType() {
        return stoneType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OmokStonePlacement that = (OmokStonePlacement) o;
        return x == that.x
                && y == that.y
                && stoneType == that.stoneType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, stoneType);
    }

    @Override
    public String toString() {
        return "OmokStonePlacement{"
                + "x=" + x
                + ", y=" + y
                + ", stoneType=" + stoneType
                + '}';
    }
}
