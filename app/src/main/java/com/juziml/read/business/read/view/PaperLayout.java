package com.juziml.read.business.read.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作为根布局使用，最好再填充一个ViewGroup并设置match_parent
 * 必须开启硬件加速，否则掉帧
 * create by zhusw on 2020-07-28 16:00
 */
public class PaperLayout extends LinearLayout {
    private final Canvas viewScreenShotCanvas;

    private ReadRecyclerView readRecyclerView;
    private final RectF menuBounds;

    public PaperLayout(@NonNull Context context) {
        this(context, null);
    }

    public PaperLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaperLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewScreenShotCanvas = new Canvas();
        menuBounds = new RectF();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        readRecyclerView = (ReadRecyclerView) getParent();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (readRecyclerView.getFlipMode() == ReadLayoutManager.BookFlipMode.MODE_CURL
                || readRecyclerView.getFlipMode() == ReadLayoutManager.BookFlipMode.MODE_COVER) {
            requestDisallowInterceptTouchEvent(true);
        } else {
            requestDisallowInterceptTouchEvent(false);
        }
        menuBounds.left = getWidth() / 3F;
        menuBounds.top = getHeight() / 3F;
        menuBounds.right = getWidth() * 2 / 3F;
        menuBounds.bottom = getHeight() * 2 / 3F;
    }

    private int offset = -2;

    /**
     * 这个会被调用多次，最终宽度为实际测量宽度-2px
     * 这样在 layoutManager 进行布局时 才可以同时保持3个item被显示
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(5, heightMeasureSpec);
        int width = measureSize(5, widthMeasureSpec);
        setMeasuredDimension(width + offset, height);
    }


    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    public Bitmap drawViewScreenShotToBitmap(Bitmap bitmap) {
        viewScreenShotCanvas.setBitmap(bitmap);
        draw(viewScreenShotCanvas);
        return bitmap;
    }

    @Override
    public boolean isScrollContainer() {
        return false;
    }


    private float interceptDownX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //动画执行期间 子view 也不可获取事件
        if (readRecyclerView.animRunning()) return true;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            interceptDownX = ev.getRawX();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float currentX = ev.getRawX();
            float distance = Math.abs(currentX - interceptDownX);
            if (distance > 1F) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float receiveDownX = -1;
    private float downX = -1;
    private float downY = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            receiveDownX = ev.getRawX();
            downX = ev.getRawX();
            downY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (receiveDownX == -1) {
                ev.setAction(MotionEvent.ACTION_DOWN);
                receiveDownX = ev.getRawX();
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            receiveDownX = -1;
        }
        if (readRecyclerView.getFlipMode() == ReadLayoutManager.BookFlipMode.MODE_NORMAL) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                float upX = ev.getRawX();
                float upY = ev.getRawY();
                if (downX > menuBounds.left && downY > menuBounds.top
                        && downX < menuBounds.right && downY < menuBounds.bottom
                        && upX > menuBounds.left && upY > menuBounds.top
                        && upX < menuBounds.right && upY < menuBounds.bottom) {
                    readRecyclerView.onClickMenu();
                } else if (upX >= getWidth() / 2F) {
                    readRecyclerView.onExpectNext(true);
                } else if (upX < getWidth() / 2F) {
                    readRecyclerView.onExpectPrevious(true);
                }
            }
        } else {
            readRecyclerView.onItemViewTouchEvent(ev);
        }
        return true;
    }
}
