package com.example.core.sprite;

// 1) 한 칸 정의
public final class SpriteRect {
    public final int index;   // row-major 부여
    public final int row, col;
    public final int x, y;    // 좌상단
    public final int width, height;

    public SpriteRect(int index, int row, int col, int x, int y, int width, int height) {
        this.index = index; this.row = row; this.col = col;
        this.x = x; this.y = y; this.width = width; this.height = height;
    }
}