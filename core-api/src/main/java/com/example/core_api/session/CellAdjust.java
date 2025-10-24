package com.example.core_api.session;

public final class CellAdjust {
    // 델타 모드
    public final int dx, dy, dWidth, dHeight;
    // 절대치 모드(옵션). null이면 미사용.
    public final Integer absX, absY, absW, absH;

    private CellAdjust(int dx, int dy, int dWidth, int dHeight,
                       Integer absX, Integer absY, Integer absW, Integer absH) {
        this.dx = dx; this.dy = dy; this.dWidth = dWidth; this.dHeight = dHeight;
        this.absX = absX; this.absY = absY; this.absW = absW; this.absH = absH;
    }
    public static CellAdjust delta(int dx, int dy, int dW, int dH) {
        return new CellAdjust(dx, dy, dW, dH, null, null, null, null);
    }
    public static CellAdjust absolute(Integer x, Integer y, Integer w, Integer h) {
        return new CellAdjust(0, 0, 0, 0, x, y, w, h);
    }
}