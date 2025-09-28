package com.example.core.sprite.provider;

import android.graphics.Path;
import android.graphics.RectF;

import com.example.core.sprite.SpriteView;

public class RoundRectClip implements SpriteView.ClipPathProvider {
    private final float rx, ry;
    public RoundRectClip(float rx, float ry) {
        this.rx = rx;
        this.ry = ry;
    }
    @Override
    public Path provide(int viewW, int viewH, RectF dstRect) {
        Path path = new Path();
        path.addRoundRect(dstRect, rx, ry, Path.Direction.CW);
        return path;
    }
}
