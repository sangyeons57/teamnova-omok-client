package com.example.application.session;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot of the Omok board.
 */
public final class OmokBoardState {

    private final int width;
    private final int height;
    private final OmokStoneType[] cells;

    private OmokBoardState(int width,
                           int height,
                           @NonNull OmokStoneType[] cells) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        int expectedSize = width * height;
        if (cells.length != expectedSize) {
            throw new IllegalArgumentException("cells length " + cells.length + " must equal width*height " + expectedSize);
        }
        this.width = width;
        this.height = height;
        this.cells = Arrays.copyOf(cells, cells.length);
    }

    public static OmokBoardState empty() {
        return new OmokBoardState(0, 0, new OmokStoneType[0]);
    }

    public static OmokBoardState create(int width, int height) {
        OmokStoneType[] cells = new OmokStoneType[Math.max(0, width * height)];
        Arrays.fill(cells, OmokStoneType.EMPTY);
        return new OmokBoardState(width, height, cells);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @NonNull
    public OmokStoneType getStone(int x, int y) {
        ensureInBounds(x, y);
        return cells[indexOf(x, y)];
    }

    @NonNull
    public List<OmokStonePlacement> getPlacements() {
        if (cells.length == 0) {
            return Collections.emptyList();
        }
        List<OmokStonePlacement> placements = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                OmokStoneType type = cells[indexOf(x, y)];
                if (type != null && type.isPlaced()) {
                    placements.add(new OmokStonePlacement(x, y, type));
                }
            }
        }
        return Collections.unmodifiableList(placements);
    }

    public boolean isEmpty() {
        for (OmokStoneType type : cells) {
            if (type != null && type.isPlaced()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public OmokBoardState withStone(@NonNull OmokStonePlacement placement) {
        Objects.requireNonNull(placement, "placement");
        ensureInBounds(placement.getX(), placement.getY());
        OmokStoneType[] nextCells = Arrays.copyOf(cells, cells.length);
        nextCells[indexOf(placement.getX(), placement.getY())] = placement.getStoneType();
        return new OmokBoardState(width, height, nextCells);
    }

    @NonNull
    public OmokBoardState withoutStone(int x, int y) {
        ensureInBounds(x, y);
        OmokStoneType[] nextCells = Arrays.copyOf(cells, cells.length);
        nextCells[indexOf(x, y)] = OmokStoneType.EMPTY;
        return new OmokBoardState(width, height, nextCells);
    }

    @NonNull
    public OmokBoardState clear() {
        if (cells.length == 0) {
            return this;
        }
        return create(width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OmokBoardState that = (OmokBoardState) o;
        return width == that.width
                && height == that.height
                && Arrays.equals(cells, that.cells);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height);
        result = 31 * result + Arrays.hashCode(cells);
        return result;
    }

    @Override
    public String toString() {
        return "OmokBoardState{"
                + "width=" + width
                + ", height=" + height
                + ", cells=" + Arrays.toString(cells)
                + '}';
    }

    private void ensureInBounds(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IndexOutOfBoundsException("x=" + x + ", y=" + y + " out of bounds for " + width + "x" + height + " board");
        }
    }

    private int indexOf(int x, int y) {
        return y * width + x;
    }
}
