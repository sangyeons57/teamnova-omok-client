package com.example.core.sprite;


import java.util.Collections;
import java.util.List;

public final class SpriteLayout {
    private final List<SpriteRect> rects;
    private final Meta meta;

    SpriteLayout(List<SpriteRect> rects, Meta meta) {
        this.rects = Collections.unmodifiableList(rects);
        this.meta = meta;
    }

    public List<SpriteRect> rects() { return rects; }
    public Meta meta() { return meta; }

    public int size() { return rects.size(); }
    public SpriteRect get(int index) { return rects.get(index); }
    public SpriteRect get(int row, int col) { return rects.get(row * meta.cols + col); }

    /** 선택: 캔버스/비트맵 경계 검증 */
    public void validateAgainst(int sheetW, int sheetH) {
        for (SpriteRect r : rects) {
            if (r.x < 0 || r.y < 0 || r.x + r.width > sheetW || r.y + r.height > sheetH) {
                throw new IllegalStateException("Rect out of bounds at index " + r.index);
            }
        }
    }

    public record Meta(int rows, int cols, int marginLeft, int marginTop, int marginRight,
                       int marginBottom, int spacingX, int spacingY, int baseCellW, int baseCellH) {
    }
}
