package com.example.feature_home.home.presentation.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.feature_home.R;
import com.google.android.material.color.MaterialColors;

/**
 * 이거 기능에 이상한 문제있음
 * configure할떄 max - value를 해야지 정상적으로 보이는 문제인데 이거 코드 더 단순하게 만들면서
 * 수정 작업필요함 (만약 재사용 하려고 하는 경우)
 */
public class WindowedGuageView extends View {

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF drawBounds = new RectF();

    private float scoreRange = 0f;
    private float targetScore = 0f;
    private float windowBottomScore = 0f;
    private float windowTopScore = 0f;

    private float contentPixelRange = 0f;
    private float pixelsPerScore = 0f;
    private float scorePerPixel = 0f;
    private float windowScoreRange = 0f;
    private float configuredWindowSize = 0f;
    private boolean reversed = false;

    public WindowedGuageView(Context context) {
        this(context, null);
    }

    public WindowedGuageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowedGuageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int surfaceVariant = resolveColor(context,
                com.google.android.material.R.attr.colorSurfaceVariant,
                com.example.designsystem.R.color.md_theme_light_surfaceVariant,
                com.example.designsystem.R.color.md_theme_dark_surfaceVariant,
                0xFFE0E0E0);
        int primary = resolveColor(context,
                android.R.attr.colorPrimary,
                com.example.designsystem.R.color.md_theme_light_primary,
                com.example.designsystem.R.color.md_theme_dark_primary,
                0xFF42505E);

        backgroundPaint.setColor(surfaceVariant);
        backgroundPaint.setStyle(Paint.Style.FILL);

        fillPaint.setColor(primary);
        fillPaint.setStyle(Paint.Style.FILL);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScoreGaugeView, defStyleAttr, 0);
            int minAttr = array.getInt(R.styleable.ScoreGaugeView_gaugeMinScore, 0);
            int maxAttr = array.getInt(R.styleable.ScoreGaugeView_gaugeMaxScore, (int) scoreRange);
            boolean reversedAttr = array.getBoolean(R.styleable.ScoreGaugeView_gaugeReversed, false);
            scoreRange = Math.max(0f, (float) maxAttr - (float) minAttr);
            reversed = reversedAttr;
            array.recycle();
        }

        updateWindowBoundsFromExtent();
        recalculateRatios();
    }

    public void configure(float worldMin, float worldMax, float windowSize, float worldValue) {
        scoreRange = Math.max(0f, worldMax - worldMin);
        configuredWindowSize = Math.max(0f, windowSize);
        targetScore = clamp(worldValue - worldMin, 0f, scoreRange);
        windowBottomScore = clamp(windowBottomScore, 0f, scoreRange);
        windowTopScore = clamp(windowTopScore, 0f, scoreRange);
        updateWindowBoundsFromExtent();
        recalculateRatios();
        invalidate();
    }

    public void setWindowStart(float windowStart) {
        float clamped = clamp(windowStart, 0f, scoreRange);
        if (reversed) {
            windowTopScore = clamped;
        } else {
            windowBottomScore = clamped;
        }
        updateWindowBoundsFromExtent();
        invalidate();
    }

    public void setWindowStartPx(float windowStartPx) {
        setWindowStart(pixelToScore(windowStartPx));
    }

    public void setReversed(boolean reversed) {
        if (this.reversed == reversed) {
            return;
        }
        this.reversed = reversed;
        updateWindowBoundsFromExtent();
        invalidate();
    }

    public boolean isReversed() {
        return reversed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBounds.set(
                getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom()
        );

        float cornerRadius = drawBounds.width() / 4f;
        canvas.drawRoundRect(drawBounds, cornerRadius, cornerRadius, backgroundPaint);

        float windowRange = windowTopScore - windowBottomScore;
        float effectiveRange = windowRange > 0f ? windowRange : getEffectiveWindowRange();
        if (effectiveRange <= 0f) {
            return;
        }

        float fraction;
        if (reversed) {
            fraction = (windowTopScore - targetScore) / effectiveRange;
        } else {
            fraction = (targetScore - windowBottomScore) / effectiveRange;
        }

        fraction = clamp(fraction, 0f, 1f);

        if (fraction <= 0f) {
            if (!reversed) {
                canvas.drawRoundRect(drawBounds, cornerRadius, cornerRadius, fillPaint);
            }
            return;
        }
        if (fraction >= 1f) {
            if (reversed) {
                canvas.drawRoundRect(drawBounds, cornerRadius, cornerRadius, fillPaint);
            }
            return;
        }

        float fillValue = drawBounds.height() * fraction; RectF fillRect;

        if (reversed) {
            fillRect = new RectF(drawBounds.left, drawBounds.top, drawBounds.right, fillValue);
            canvas.drawRoundRect(fillRect, cornerRadius, cornerRadius, fillPaint);
        } else {
            fillRect = new RectF(drawBounds.left, fillValue, drawBounds.right, drawBounds.bottom);
        }
        canvas.drawRoundRect(fillRect, cornerRadius, cornerRadius, fillPaint);
    }

    /**
     * Declares how many pixels make up the full score ladder so the gauge can map scroll
     * offsets to score values and vice versa.
     */
    public void setContentMetrics(float pixelRange, float scoreRange) {
        contentPixelRange = Math.max(0f, pixelRange);
        if (scoreRange >= 0f) {
            this.scoreRange = Math.max(0f, scoreRange);
        }
        targetScore = clamp(targetScore, 0f, this.scoreRange);
        windowBottomScore = clamp(windowBottomScore, 0f, this.scoreRange);
        windowTopScore = clamp(windowTopScore, 0f, this.scoreRange);
        recalculateRatios();
        invalidate();
    }

    public float scoreToPixel(float score) {
        if (score <= 0f) {
            return 0f;
        }
        if (pixelsPerScore <= 0f) {
            float range = getWorldSize();
            float height = getContentHeight();
            if (range <= 0f || height <= 0f) {
                return 0f;
            }
            return (score / range) * height;
        }
        return Math.min(score, scoreRange) * pixelsPerScore;
    }

    public float pixelToScore(float pixels) {
        if (pixels <= 0f) {
            return 0f;
        }
        if (scorePerPixel <= 0f) {
            float height = getContentHeight();
            float range = getWorldSize();
            if (height <= 0f || range <= 0f) {
                return 0f;
            }
            return (pixels / height) * range;
        }
        float score = pixels * scorePerPixel;
        return clamp(score, 0f, scoreRange);
    }

    public float getWorldSize() {
        return scoreRange;
    }

    private void recalculateRatios() {
        float range = scoreRange;
        if (range <= 0f) {
            pixelsPerScore = 0f;
            scorePerPixel = 0f;
            windowScoreRange = 0f;
            targetScore = 0f;
            windowBottomScore = 0f;
            windowTopScore = 0f;
            updateWindowBoundsFromExtent();
            return;
        }

        float contentHeight = getContentHeight();
        float pixelRange = contentPixelRange > 0f ? contentPixelRange : contentHeight;

        if (pixelRange <= 0f) {
            pixelsPerScore = 0f;
            scorePerPixel = 0f;
            windowScoreRange = configuredWindowSize > 0f ? Math.min(range, configuredWindowSize) : range;
            targetScore = clamp(targetScore, 0f, range);
            windowBottomScore = clamp(windowBottomScore, 0f, range);
            windowTopScore = clamp(windowTopScore, 0f, range);
            updateWindowBoundsFromExtent();
            return;
        }

        pixelsPerScore = pixelRange / range;
        scorePerPixel = range / pixelRange;

        if (contentHeight > 0f) {
            windowScoreRange = Math.min(range, contentHeight * scorePerPixel);
        } else if (configuredWindowSize > 0f) {
            windowScoreRange = Math.min(range, configuredWindowSize);
        } else {
            windowScoreRange = range;
        }

        targetScore = clamp(targetScore, 0f, range);
        windowBottomScore = clamp(windowBottomScore, 0f, range);
        windowTopScore = clamp(windowTopScore, 0f, range);
        updateWindowBoundsFromExtent();
    }

    private float getEffectiveWindowRange() {
        if (windowScoreRange > 0f) {
            return windowScoreRange;
        }
        float diff = windowTopScore - windowBottomScore;
        if (diff > 0f) {
            return diff;
        }
        if (configuredWindowSize > 0f) {
            return Math.min(configuredWindowSize, scoreRange);
        }
        return scoreRange;
    }

    private void updateWindowBoundsFromExtent() {
        float range = scoreRange;
        if (range <= 0f) {
            windowBottomScore = 0f;
            windowTopScore = 0f;
            return;
        }

        windowBottomScore = clamp(windowBottomScore, 0f, range);
        windowTopScore = clamp(windowTopScore, 0f, range);

        float extent = getEffectiveWindowRange();
        if (extent <= 0f) {
            if (reversed) {
                if (windowTopScore < windowBottomScore) {
                    windowTopScore = windowBottomScore;
                }
            } else if (windowTopScore < windowBottomScore) {
                windowTopScore = windowBottomScore;
            }
            return;
        }

        if (reversed) {
            if (windowTopScore <= 0f) {
                windowTopScore = clamp(windowBottomScore + extent, 0f, range);
            }
            windowBottomScore = clamp(windowTopScore - extent, 0f, range);
        } else {
            if (windowBottomScore >= range) {
                windowBottomScore = clamp(range - extent, 0f, range);
            }
            windowTopScore = clamp(windowBottomScore + extent, 0f, range);
        }
    }

    private float getContentHeight() {
        return Math.max(0f, getHeight() - getPaddingTop() - getPaddingBottom());
    }

    private static float clamp(float value, float min, float max) {
        if (max < min) {
            return min;
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recalculateRatios();
        invalidate();
    }

    private int resolveColor(Context context,
                             int attrRes,
                             int lightColorRes,
                             int darkColorRes,
                             int fallbackColor) {
        boolean isNightMode = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        int fallback = ContextCompat.getColor(context, isNightMode ? darkColorRes : lightColorRes);
        return MaterialColors.getColor(context, attrRes, fallback != 0 ? fallback : fallbackColor);
    }

}
