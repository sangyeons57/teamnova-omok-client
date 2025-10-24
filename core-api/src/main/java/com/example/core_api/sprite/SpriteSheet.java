package com.example.core_api.sprite;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public final class SpriteSheet {
    private final Bitmap sheet;
    private final SpriteLayout layout;
    private final Paint defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public SpriteSheet(Bitmap sheet, SpriteLayout layout) {
        if (sheet == null || layout == null) throw new IllegalArgumentException();
        // 선택: 안전검증
        layout.validateAgainst(sheet.getWidth(), sheet.getHeight());
        this.sheet = sheet; this.layout = layout;
    }

    public int size() { return layout.size(); }
    public SpriteRect rect(int index) { return layout.get(index); }

    public Bitmap getBitmap(int index) {
        SpriteRect r = rect(index);
        return Bitmap.createBitmap(sheet, r.x, r.y, r.width, r.height);
    }

    public void draw(Canvas canvas, int index, RectF dst) {
        SpriteRect r = rect(index);
        Rect src = new Rect(r.x, r.y, r.x + r.width, r.y + r.height);
        canvas.drawBitmap(sheet, src, dst, defaultPaint);
    }

    public void draw(Canvas canvas, int index, float dx, float dy, float scale) {
        SpriteRect r = rect(index);
        Rect src = new Rect(r.x, r.y, r.x + r.width, r.y + r.height);
        RectF dst = new RectF(dx, dy, dx + r.width * scale, dy + r.height * scale);
        canvas.drawBitmap(sheet, src, dst, defaultPaint);
    }

    public Bitmap bitmap() { return sheet; }
    public SpriteLayout layout() { return layout; }
}