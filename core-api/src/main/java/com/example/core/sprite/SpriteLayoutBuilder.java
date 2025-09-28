package com.example.core.sprite;


import com.example.core.session.CellAdjust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class SpriteLayoutBuilder {
    // 필수
    private int rows = -1, cols = -1;

    // 기본값
    private int marginLeft=0, marginTop=0, marginRight=0, marginBottom=0;
    private int spacingX=0, spacingY=0;
    private int baseCellW=-1, baseCellH=-1;

    // 미세설정: (row,col) 키
    private final Map<Long, CellAdjust> perCell = new HashMap<>();

    // === 체이닝 API ===
    public SpriteLayoutBuilder rows(int rows){ this.rows = rows; return this; }
    public SpriteLayoutBuilder cols(int cols){ this.cols = cols; return this; }
    public SpriteLayoutBuilder grid(int rows, int cols){
        this.rows = rows; this.cols = cols; return this;
    }

    public SpriteLayoutBuilder margin(int all){
        this.marginLeft=all; this.marginTop=all; this.marginRight=all; this.marginBottom=all; return this;
    }
    public SpriteLayoutBuilder margin(int left, int top, int right, int bottom){
        this.marginLeft=left; this.marginTop=top; this.marginRight=right; this.marginBottom=bottom; return this;
    }

    public SpriteLayoutBuilder spacing(int x, int y){ this.spacingX=x; this.spacingY=y; return this; }
    public SpriteLayoutBuilder cellSize(int w, int h){ this.baseCellW=w; this.baseCellH=h; return this; }

    /** index 기반 */
    public SpriteLayoutBuilder adjustIndex(int index, CellAdjust adj){
        int r = index / ensure(cols, "cols");
        int c = index % ensure(cols, "cols");
        return adjust(r, c, adj);
    }

    /** row,col 기반 */
    public SpriteLayoutBuilder adjust(int row, int col, CellAdjust adj){
        perCell.put(key(row, col), adj); return this;
    }

    // === 빌드 ===
    public SpriteLayout build() {
        // 1) 필수 검증
        ensure(rows, "rows"); ensure(cols, "cols");
        ensure(baseCellW, "baseCellW"); ensure(baseCellH, "baseCellH");
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("rows/cols must be >0");
        if (baseCellW <= 0 || baseCellH <= 0) throw new IllegalArgumentException("cell size must be >0");

        // 2) 기본 그리드 생성
        ArrayList<SpriteRect> out = new ArrayList<>(rows * cols);
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = marginLeft + c * (baseCellW + spacingX);
                int y = marginTop  + r * (baseCellH + spacingY);
                int w = baseCellW, h = baseCellH;

                // 3) per-cell 미세설정 적용
                CellAdjust adj = perCell.get(key(r, c));
                if (adj != null) {
                    if (adj.absX != null) x = adj.absX;
                    if (adj.absY != null) y = adj.absY;
                    if (adj.absW != null) w = adj.absW;
                    if (adj.absH != null) h = adj.absH;
                    x += adj.dx; y += adj.dy; w += adj.dWidth; h += adj.dHeight;
                }
                out.add(new SpriteRect(index, r, c, x, y, w, h));
                index++;
            }
        }

        SpriteLayout.Meta meta = new SpriteLayout.Meta(
                rows, cols,
                marginLeft, marginTop, marginRight, marginBottom,
                spacingX, spacingY,
                baseCellW, baseCellH
        );

        return new SpriteLayout( out, meta );
    }

    // 선택: 비트맵 크기 받아 즉시 경계검증까지
    public SpriteLayout buildAndValidate(int sheetW, int sheetH) {
        SpriteLayout layout = build();
        layout.validateAgainst(sheetW, sheetH);
        return layout;
    }

    // === 내부 유틸 ===
    private static long key(int r, int c){ return (((long)r) << 32) | (c & 0xffffffffL); }
    private static int ensure(int v, String name){
        if (v < 0) throw new IllegalStateException("missing: " + name);
        return v;
    }
}