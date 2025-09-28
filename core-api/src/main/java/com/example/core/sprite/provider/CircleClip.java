package com.example.core.sprite.provider;

import android.graphics.Path;
import android.graphics.RectF;

import com.example.core.sprite.SpriteView;

public final class CircleClip implements SpriteView.ClipPathProvider {
    @Override
    public Path provide(int viewW, int viewH, RectF dstRect) {
        float rad = Math.min(dstRect.width(), dstRect.height()) / 2f;
        Path path = new Path();
        path.addCircle(dstRect.centerX(), dstRect.centerY(), rad, Path.Direction.CW);
        return path;
    }
}
