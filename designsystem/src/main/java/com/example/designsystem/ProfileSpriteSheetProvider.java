package com.example.designsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.core.session.CellAdjust;
import com.example.core.sprite.SpriteLayout;
import com.example.core.sprite.SpriteLayoutBuilder;
import com.example.core.sprite.SpriteSheet;

/**
 * Lazily provides the sprite sheet that contains the selectable profile icons.
 */
public final class ProfileSpriteSheetProvider {

    private static final Object LOCK = new Object();

    private static final SpriteLayout PROFILE_LAYOUT = new SpriteLayoutBuilder()
            .grid(2, 5)
            .margin(62,42,0,0)
            .cellSize(250, 250)
            .spacing(67, 44)
            .adjust(0,0 , CellAdjust.delta(0,0,0,0))
            .adjust(1,0 , CellAdjust.delta(0,0,0,0))
            .build();

    private static volatile SpriteSheet cachedSheet;

    private ProfileSpriteSheetProvider() {
    }

    @NonNull
    public static SpriteSheet get(@NonNull Context context) {
        SpriteSheet sheet = cachedSheet;
        if (sheet == null || sheet.bitmap().isRecycled()) {
            synchronized (LOCK) {
                sheet = cachedSheet;
                if (sheet == null || sheet.bitmap().isRecycled()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            context.getApplicationContext().getResources(),
                            R.drawable.icon_profile,
                            options
                    );
                    sheet = new SpriteSheet(bitmap, PROFILE_LAYOUT);
                    cachedSheet = sheet;
                }
            }
        }
        return sheet;
    }
}
