package com.example.core.sprite;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public final class SpriteView extends View {

    public interface ClipPathProvider {
        Path provide(int viewW, int viewH, RectF dstRect);
    }

    public interface ShaderProvider {
        Shader provide(Bitmap sheet, Rect srcRect, RectF dstRect);
    }

    private @Nullable ClipPathProvider clipper;
    private @Nullable ShaderProvider shaderer;

    public enum ScaleMode { CENTER, FIT_CENTER, FILL } // 기본만 제공

    private @Nullable SpriteSheet sheet;
    private int index = 0;
    private ScaleMode scaleMode = ScaleMode.FIT_CENTER;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SpriteView(Context c) { super(c); }
    public SpriteView(Context c, @Nullable AttributeSet a) { super(c, a); }
    public SpriteView(Context c, @Nullable AttributeSet a, int s) { super(c, a, s); }

    // ---------- 공개 API ----------

    public void setClipPathProvider(@Nullable ClipPathProvider p) {
        this.clipper = p; invalidate();
    }
    public void setShaderProvider(@Nullable ShaderProvider p) {
        this.shaderer = p; invalidate();
    }

    /** 시트 교체 */
    public void setSpriteSheet(@Nullable SpriteSheet s) {
        this.sheet = s;
        requestLayout();
        invalidate();
    }

    /** 표시할 인덱스 교체 */
    public void setIndex(int idx) {
        this.index = idx;
        requestLayout(); // 셀마다 크기가 다를 수 있음
        invalidate();
    }

    /** 배치 모드 */
    public void setScaleMode(ScaleMode mode) {
        this.scaleMode = mode != null ? mode : ScaleMode.FIT_CENTER;
        invalidate();
    }

    /** 색 보정 등 필요 시 */
    public void setColorFilter(@Nullable ColorFilter cf) {
        paint.setColorFilter(cf);
        shaderPaint.setColorFilter(cf);
        invalidate();
    }

    /** 알파(0~255) */
    @Override public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        int a = (int)(Math.max(0f, Math.min(1f, alpha)) * 255f);
        paint.setAlpha(a);
        shaderPaint.setAlpha(a);
        invalidate();
    }

    // ---------- 레이아웃 ----------
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int padH = getPaddingLeft() + getPaddingRight();
        int padV = getPaddingTop() + getPaddingBottom();

        int desiredW = 0, desiredH = 0;
        SpriteRect r = currentRect();
        if (r != null) {
            desiredW = r.width + padH;
            desiredH = r.height + padV;
        }

        int w = resolveSize(desiredW, widthMeasureSpec);
        int h = resolveSize(desiredH, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    // ---------- 그리기 ----------
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sheet == null) return;

        SpriteRect r = currentRect();
        if (r == null) return;

        // 패딩 반영
        int vw = getWidth() - getPaddingLeft() - getPaddingRight();
        int vh = getHeight() - getPaddingTop() - getPaddingBottom();
        if (vw <= 0 || vh <= 0) return;

        Rect src = new Rect(r.x, r.y, r.x + r.width, r.y + r.height);
        RectF dst = computeDst(r.width, r.height, vw, vh, scaleMode);

        // 패딩 오프셋
        dst.offset(getPaddingLeft(), getPaddingTop());

        int save = canvas.save();
        // 1) 선택적 클립핑
        if (clipper != null) {
            Path path = clipper.provide(getWidth(), getHeight(), dst);
            if(path != null) canvas.clipPath(path);
        }
        // 2) 선택적 셰이더 랜더링
        if (shaderer != null) {
            Shader sh = shaderer.provide(sheet.bitmap(), src, dst);
            if (sh != null) {
                shaderPaint.setShader(sh);

                canvas.drawRect(dst, shaderPaint);
                shaderPaint.setShader(null);
                canvas.restoreToCount(save);
                return;
            }
        }

        // 기본 비트맵 랜더
        canvas.drawBitmap(sheet.bitmap(), src, dst, paint);
        canvas.restoreToCount(save);
    }

    // ---------- 내부 유틸 ----------
    private @Nullable SpriteRect currentRect() {
        if (sheet == null) return null;
        int size = sheet.layout().size();
        if (size <= 0) return null;
        int safe = Math.max(0, Math.min(index, size - 1));
        return sheet.layout().get(safe);
    }

    private static RectF computeDst(int sw, int sh, int vw, int vh, ScaleMode mode) {
        if(sw <= 0 || sh <= 0 || vw <= 0 || vh <= 0) return new RectF(0, 0, 0, 0);
        if (mode == ScaleMode.CENTER) {
            float dx = (vw - sw) * 0.5f;
            float dy = (vh - sh) * 0.5f;
            return new RectF(dx, dy, dx + sw, dy + sh);
        }
        float sx = vw / (float) sw;
        float sy = vh / (float) sh;
        float scale = (mode == ScaleMode.FILL) ? Math.max(sx, sy) : Math.min(sx, sy);
        float dw = sw * scale, dh = sh * scale;
        float left = (vw - dw) * 0.5f;
        float top  = (vh - dh) * 0.5f;
        return new RectF(left, top, left + dw, top + dh);
    }
}