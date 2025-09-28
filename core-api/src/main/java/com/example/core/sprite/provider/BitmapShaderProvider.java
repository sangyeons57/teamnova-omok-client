package com.example.core.sprite.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.example.core.sprite.SpriteView;

public class BitmapShaderProvider implements SpriteView.ShaderProvider {
    @Override
    public Shader provide(Bitmap sheet, Rect src, RectF dst) {
        BitmapShader sh = new BitmapShader(sheet, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT);

        Matrix m = new Matrix();

        m.postTranslate(-src.left, -src.top);
        float sx = dst.width() / (float) src.width();
        float sy = dst.height() / (float) src.height();
        m.postScale(sx, sy);

        m.postTranslate(dst.left, dst.top);
        sh.setLocalMatrix(m);
        return sh;
    }
}
