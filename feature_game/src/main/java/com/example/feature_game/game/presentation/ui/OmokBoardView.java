package com.example.feature_game.game.presentation.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.feature_game.R;
import com.google.android.material.color.MaterialColors;

/**
 * Simple Omok board rendering view that can register drawable indices and report tap locations.
 */
public class OmokBoardView extends View {

    private static final int DEFAULT_BOARD_SIZE = 10;
    private static final int EMPTY_CELL = -1;
    private static final String TAG = "OmokBoardView";

    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SparseArray<android.graphics.drawable.Drawable> drawableRegistry = new SparseArray<>();
    private final Rect drawableBounds = new Rect();
    private final RectF contentBounds = new RectF();
    private final Paint coordinateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int boardWidth;
    private int boardHeight;
    private float gridStrokeWidth;
    private float borderStrokeWidth;
    private float stoneRadiusRatio;
    private int[] cellIndices = new int[0];
    private OnCellTapListener cellTapListener;
    private int lastTappedX = -1;
    private int lastTappedY = -1;
    private boolean interactionEnabled = true;

    public OmokBoardView(@NonNull Context context) {
        this(context, null);
    }

    public OmokBoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OmokBoardView(@NonNull Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        gridStrokeWidth = dpToPx(1.5f);
        borderStrokeWidth = dpToPx(2f);
        stoneRadiusRatio = 0.4f;
        int fallbackGridColor = Color.parseColor("#B0A37F");
        int gridColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutlineVariant, fallbackGridColor);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(gridStrokeWidth);
        gridPaint.setColor(gridColor);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderStrokeWidth);
        int fallbackBorderColor = Color.parseColor("#8A795D");
        int borderColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutline, fallbackBorderColor);
        borderPaint.setColor(borderColor);

        int coordinateColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface, Color.WHITE);
        coordinateTextPaint.setStyle(Paint.Style.FILL);
        coordinateTextPaint.setColor(coordinateColor);
        coordinateTextPaint.setTextSize(spToPx(14f));

        int columns = DEFAULT_BOARD_SIZE;
        int rows = DEFAULT_BOARD_SIZE;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OmokBoardView, defStyleAttr, 0);
            columns = ta.getInt(R.styleable.OmokBoardView_boardColumns, DEFAULT_BOARD_SIZE);
            rows = ta.getInt(R.styleable.OmokBoardView_boardRows, DEFAULT_BOARD_SIZE);
            gridStrokeWidth = ta.getDimension(R.styleable.OmokBoardView_gridLineWidth, gridStrokeWidth);
            gridPaint.setStrokeWidth(gridStrokeWidth);
            int resolvedGridColor = ta.getColor(R.styleable.OmokBoardView_gridLineColor, gridColor);
            gridPaint.setColor(resolvedGridColor);
            int resolvedBorderColor = ta.getColor(R.styleable.OmokBoardView_gridBorderColor, borderPaint.getColor());
            borderPaint.setColor(resolvedBorderColor);
            borderStrokeWidth = ta.getDimension(R.styleable.OmokBoardView_gridBorderWidth, borderStrokeWidth);
            borderPaint.setStrokeWidth(borderStrokeWidth);
            stoneRadiusRatio = ta.getFloat(R.styleable.OmokBoardView_stoneRadiusRatio, stoneRadiusRatio);
            if (stoneRadiusRatio <= 0f || stoneRadiusRatio > 0.5f) {
                stoneRadiusRatio = 0.4f;
            }
            ta.recycle();
        }
        setBoardSize(columns, rows);
        setClickable(true);
        setFocusable(true);
    }

    /**
     * Registers a drawable resource for the provided index.
     */
    public void registerStoneDrawable(int index, @DrawableRes int drawableRes) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        android.graphics.drawable.Drawable drawable = AppCompatResources.getDrawable(getContext(), drawableRes);
        if (drawable != null) {
            drawableRegistry.put(index, DrawableCompat.wrap(drawable.mutate()));
            invalidate();
        }
    }

    /**
     * Registers a drawable instance for the provided index.
     */
    public void registerStoneDrawable(int index, @NonNull android.graphics.drawable.Drawable drawable) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        drawableRegistry.put(index, DrawableCompat.wrap(drawable.mutate()));
        invalidate();
    }

    /**
     * Clears all registered stone drawables.
     */
    public void clearStoneDrawables() {
        drawableRegistry.clear();
        invalidate();
    }

    /**
     * Sets the entire board state in one call.
     *
     * @param width  number of board columns
     * @param height number of board rows
     * @param indices flattened indices (length {@code width * height}) or {@code null} to clear.
     */
    public void setBoardState(int width, int height, @Nullable int[] indices) {
        setBoardSize(width, height);
        if (indices != null && indices.length == boardWidth * boardHeight) {
            System.arraycopy(indices, 0, cellIndices, 0, indices.length);
        } else {
            clearCellsInternal();
            lastTappedX = -1;
            lastTappedY = -1;
        }
        invalidate();
    }

    /**
     * Updates the index for a specific cell.
     */
    public void setCellDrawableIndex(int x, int y, int index) {
        if (!ensureInBounds(x, y)) {
            return;
        }
        int clamped = index >= 0 ? index : EMPTY_CELL;
        int newIndex = flattenIndex(x, y);
        cellIndices[newIndex] = clamped;
        invalidate();
    }

    /**
     * Returns the currently stored index for a cell, or {@code -1} if empty.
     */
    public int getCellDrawableIndex(int x, int y) {
        if (!ensureInBounds(x, y)) {
            return EMPTY_CELL;
        }
        return cellIndices[flattenIndex(x, y)];
    }

    /**
     * Clears the board without altering the registered drawables.
     */
    public void clearBoard() {
        clearCellsInternal();
        lastTappedX = -1;
        lastTappedY = -1;
        invalidate();
    }

    /**
     * Updates the board size and clears previous cell indices.
     */
    public void setBoardSize(@IntRange(from = 0) int width,
                             @IntRange(from = 0) int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Board dimensions must be >= 0");
        }
        if (width == boardWidth && height == boardHeight) {
            return;
        }
        boardWidth = width;
        boardHeight = height;
        int total = boardWidth * boardHeight;
        cellIndices = total > 0 ? new int[total] : new int[0];
        clearCellsInternal();
        lastTappedX = -1;
        lastTappedY = -1;
        invalidate();
    }

    public void setOnCellTapListener(@Nullable OnCellTapListener listener) {
        this.cellTapListener = listener;
    }

    /**
     * Enables or disables board interactions while keeping the view rendered.
     */
    public void setBoardInteractionEnabled(boolean enabled) {
        if (interactionEnabled == enabled) {
            return;
        }
        interactionEnabled = enabled;
        setClickable(enabled);
        setFocusable(enabled);
        if (!enabled) {
            lastTappedX = -1;
            lastTappedY = -1;
            invalidate();
        }
    }

    /**
     * Returns {@code true} when tap interactions are allowed.
     */
    public boolean isBoardInteractionEnabled() {
        return interactionEnabled;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (boardWidth <= 0 || boardHeight <= 0) {
            return;
        }

        updateContentBounds();

        float contentWidth = contentBounds.width();
        float contentHeight = contentBounds.height();

        // Draw border
        canvas.drawRect(contentBounds, borderPaint);

        float cellWidth = boardWidth > 1 ? contentWidth / (boardWidth - 1f) : 0f;
        float cellHeight = boardHeight > 1 ? contentHeight / (boardHeight - 1f) : 0f;

        // Draw grid lines
        for (int x = 0; x < boardWidth; x++) {
            float lineX = contentBounds.left + (boardWidth > 1 ? x * cellWidth : contentWidth / 2f);
            canvas.drawLine(lineX, contentBounds.top, lineX, contentBounds.bottom, gridPaint);
        }
        for (int y = 0; y < boardHeight; y++) {
            float lineY = contentBounds.top + (boardHeight > 1 ? y * cellHeight : contentHeight / 2f);
            canvas.drawLine(contentBounds.left, lineY, contentBounds.right, lineY, gridPaint);
        }

        if (cellIndices.length == 0) {
            return;
        }
        float stoneDiameter = stoneRadiusRatio * 2f * Math.min(
                boardWidth > 1 ? cellWidth : contentWidth,
                boardHeight > 1 ? cellHeight : contentHeight
        );
        float stoneRadius = stoneDiameter / 2f;
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                int index = cellIndices[flattenIndex(x, y)];
                if (index < 0) {
                    continue;
                }
                android.graphics.drawable.Drawable drawable = drawableRegistry.get(index);
                if (drawable == null) {
                    continue;
                }
                float centerX = contentBounds.left + (boardWidth > 1 ? x * cellWidth : contentWidth / 2f);
                float centerY = contentBounds.top + (boardHeight > 1 ? y * cellHeight : contentHeight / 2f);
                drawableBounds.set(
                        (int) (centerX - stoneRadius),
                        (int) (centerY - stoneRadius),
                        (int) (centerX + stoneRadius),
                        (int) (centerY + stoneRadius)
                );
                drawable.setBounds(drawableBounds);
                drawable.draw(canvas);
            }
        }

        if (lastTappedX >= 0 && lastTappedY >= 0) {
            String text = "x:" + lastTappedX + ", y:" + lastTappedY;
            float textX = contentBounds.left + dpToPx(8f);
            float textY = contentBounds.bottom - dpToPx(8f);
            canvas.drawText(text, textX, textY, coordinateTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!interactionEnabled || !isEnabled() || boardWidth <= 0 || boardHeight <= 0) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int cellX = resolveColumn(event.getX());
                int cellY = resolveRow(event.getY());
                if (cellX >= 0 && cellY >= 0) {
                    lastTappedX = cellX;
                    lastTappedY = cellY;
                    invalidate();
                    Log.d(TAG, "Cell tapped at x=" + cellX + ", y=" + cellY);
                    if (cellTapListener != null) {
                        performClick();
                        cellTapListener.onCellTapped(cellX, cellY);
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                lastTappedX = -1;
                lastTappedY = -1;
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void clearCellsInternal() {
        for (int i = 0; i < cellIndices.length; i++) {
            cellIndices[i] = EMPTY_CELL;
        }
    }

    private boolean ensureInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < boardWidth && y < boardHeight && cellIndices.length == boardWidth * boardHeight;
    }

    private int flattenIndex(int x, int y) {
        return y * boardWidth + x;
    }

    private int resolveColumn(float touchX) {
        updateContentBounds();
        float left = contentBounds.left;
        float right = contentBounds.right;
        if (touchX < left || touchX > right) {
            return -1;
        }
        if (boardWidth <= 1) {
            return 0;
        }
        float contentWidth = contentBounds.width();
        if (contentWidth <= 0f) {
            return -1;
        }
        float cellWidth = contentWidth / (boardWidth - 1f);
        int column = Math.round((touchX - left) / cellWidth);
        if (column < 0) {
            column = 0;
        } else if (column >= boardWidth) {
            column = boardWidth - 1;
        }
        return column;
    }

    private int resolveRow(float touchY) {
        updateContentBounds();
        float top = contentBounds.top;
        float bottom = contentBounds.bottom;
        if (touchY < top || touchY > bottom) {
            return -1;
        }
        if (boardHeight <= 1) {
            return 0;
        }
        float contentHeight = contentBounds.height();
        if (contentHeight <= 0f) {
            return -1;
        }
        float cellHeight = contentHeight / (boardHeight - 1f);
        int row = Math.round((touchY - top) / cellHeight);
        if (row < 0) {
            row = 0;
        } else if (row >= boardHeight) {
            row = boardHeight - 1;
        }
        return row;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private void updateContentBounds() {
        float availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float side = Math.max(0f, Math.min(availableWidth, availableHeight));
        float left = getPaddingLeft() + (availableWidth - side) / 2f;
        float top = getPaddingTop() + (availableHeight - side) / 2f;
        contentBounds.set(left, top, left + side, top + side);
    }

    /**
     * Listener invoked when the user taps a grid intersection.
     */
    public interface OnCellTapListener {
        void onCellTapped(int x, int y);
    }
}
