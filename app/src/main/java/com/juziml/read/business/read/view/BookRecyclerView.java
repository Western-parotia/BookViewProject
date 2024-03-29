package com.juziml.read.business.read.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


/**
 * 关闭了抛投效果
 * create by zhusw on 2020-03-30 11:51
 */
public class BookRecyclerView extends RecyclerView implements RVInnerItemFunction, RVOuterFunction {

    private final BookLayoutManager layoutManager;

    private boolean allowInterceptTouchEvent = true;

    private int currentPosition = 0;
    private WeakReference<EventProxy> eventProxyWeakReference;
    private AnimParentView animParentView;
    private BookView.OnPositionChangedListener onPositionChangedListener;

    private Bitmap previousBitmap = null;
    private Bitmap currentBitmap = null;
    private Bitmap nextBitmap = null;

    public BookRecyclerView(Context context) {
        this(context, null);
    }

    public BookRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layoutManager = new BookLayoutManager(context);
        setLayoutManager(layoutManager);
        layoutManager.setOnForceLayoutCompleted(new ItemOnForceLayoutCompleted());
        layoutManager.setonStopScroller(new ItemOnScrollStop());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animParentView = (AnimParentView) getParent();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        eventProxyWeakReference.clear();
        clearBitmapCache();
    }

    protected void bindReadCurlAnimProxy(EventProxy ic) {
        if (null != eventProxyWeakReference) {
            eventProxyWeakReference.clear();
        }
        eventProxyWeakReference = new WeakReference<>(ic);
    }


    protected void setOnPositionChangedListener(BookView.OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return false;
    }

    @Override
    public void scrollToPosition(int position) {
        layoutManager.forceScrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        layoutManager.smoothScrollToPosition(position);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutManager.onRecyclerViewSizeChange();
    }


    private final List<Float> moveSampling = new LinkedList<>();
    private final int MAX_COUNT = 5;

    @Override
    public boolean isScrollContainer() {
        if (allowInterceptTouchEvent) {
            return super.isScrollContainer();
        } else {
            return false;
        }

    }

    private float downX = 0F;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!allowInterceptTouchEvent) return false;//[偶现 动画期间 产生了item滑动，这里最后杀手锏再屏蔽下]

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveSampling.clear();
                downX = e.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float mx = e.getRawX();

                if (moveSampling.size() == 0 || mx != moveSampling.get(moveSampling.size() - 1)) {
                    moveSampling.add(mx);
                }
                if (moveSampling.size() > MAX_COUNT) {
                    moveSampling.remove(0);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (moveSampling.size() > 0) {
                    float lastMoveX = moveSampling.get(moveSampling.size() - 1);
                    float firstMoveX = moveSampling.get(0);
                    float finallyMoveX = lastMoveX - firstMoveX;
                    if (lastMoveX - downX < 0) {//左滑
                        layoutManager.setAutoLeftScroll(finallyMoveX < 10);
                    } else {
                        layoutManager.setAutoLeftScroll(finallyMoveX < 0);
                    }
                    moveSampling.clear();
                } else {
                    layoutManager.setAutoLeftScroll(false);
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //交由父类处理滑动，flip = BookFlipMode.MODE_NORMAL,
        if (allowInterceptTouchEvent) {
            return super.onInterceptTouchEvent(e);
        }
        //交由子View自行处理,flip = BookFlipMode.MODE_COVER| BookFlipMode.MODE_CURL
        return false;
    }

    @Override
    public void onExpectNext(boolean smooth) {
        Adapter adapter = getAdapter();
        final int dataCount = adapter.getItemCount();
        final int nextPos = currentPosition + 1;

        if (nextPos < dataCount) {
            if (smooth) {
                smoothScrollToPosition(nextPos);
            } else {
                scrollToPosition(nextPos);
            }
        }
    }

    @Override
    public void onExpectPrevious(boolean smooth) {
        if (currentPosition - 1 >= 0) {
            if (smooth) {
                smoothScrollToPosition(currentPosition - 1);
            } else {
                scrollToPosition(currentPosition - 1);
            }
        }

    }

    protected void setFlipMode(int flipMode) {
        layoutManager.setBookFlipMode(flipMode);
        if (flipMode == BookLayoutManager.BookFlipMode.MODE_CURL || flipMode == BookLayoutManager.BookFlipMode.MODE_COVER) {
            allowInterceptTouchEvent = false;
        } else {
            allowInterceptTouchEvent = true;
        }
        layoutManager.requestLayout();
    }

    @Override
    public int getFlipMode() {
        return layoutManager.getBookFlipMode();
    }

    @Override
    public void onItemViewTouchEvent(MotionEvent event) {
        if (null != eventProxyWeakReference && null != eventProxyWeakReference.get()) {
            eventProxyWeakReference.get().onItemViewTouchEvent(event);
        }
    }

    @Override
    public boolean animRunning() {
        if (null != eventProxyWeakReference && null != eventProxyWeakReference.get()) {
            eventProxyWeakReference.get().animRunning();
        }
        return false;
    }

    @Override
    public void onClickMenu() {
        animParentView.onClickMenuArea();
    }

    private class ItemOnScrollStop implements BookLayoutManager.OnStopScroller {
        @Override
        public void onStop(boolean autoLeftScroll, int curPos) {
            boolean arriveNext = currentPosition < curPos;
            currentPosition = curPos;
            if (null != onPositionChangedListener) {
                onPositionChangedListener.onChanged(arriveNext, curPos);
            }
        }

    }

    private class ItemOnForceLayoutCompleted implements BookLayoutManager.OnForceLayoutCompleted {

        @Override
        public void onLayoutCompleted(final int curPos) {
            boolean arriveNext = currentPosition < curPos;
            currentPosition = curPos;
            if (null != onPositionChangedListener) {
                onPositionChangedListener.onChanged(arriveNext, curPos);
            }
        }
    }

    @Override
    public Bitmap getPreviousBitmap() {
        int prePos = currentPosition - 1;
        Bitmap pb = null;
        if (prePos >= 0) {
            pb = printViewToBitmap(prePos, 0);
        }
        return pb;
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return printViewToBitmap(currentPosition, 1);
    }

    @Override
    public Bitmap getNextBitmap() {
        final int dataCount = getAdapter().getItemCount();
        int nextPos = currentPosition + 1;
        Bitmap nb = null;
        if (nextPos < dataCount) {
            nb = printViewToBitmap(nextPos, 2);
        }
        return nb;
    }

    /**
     * 将view渲染结果 打印到一个bitmap上
     *
     * @param type 0 前一页，1 当前页，2 后一页
     * @return
     */
    private Bitmap printViewToBitmap(int pos, int type) {
        View view = layoutManager.findViewByPosition(pos);
        if (null != view) {
            if (view instanceof PaperLayout) {
                PaperLayout pageView = (PaperLayout) view;
                Bitmap bitmapTarget = obtainBitmap(pageView.getWidth(), pageView.getHeight(), type);
                pageView.drawViewScreenShotToBitmap(bitmapTarget);
                return bitmapTarget;
            } else {
                throw new IllegalArgumentException("item 根View必须使用 PaperLayout");
            }
        }
        return null;
    }

    private Bitmap obtainBitmap(int w, int h, int type) {
        Bitmap cache = null;
        if (type == 0) {
            previousBitmap = previousBitmap != null ? previousBitmap : createNewBitmap(w, h);
            cache = previousBitmap;
        } else if (type == 1) {
            currentBitmap = currentBitmap != null ? currentBitmap : createNewBitmap(w, h);
            cache = currentBitmap;
        } else if (type == 2) {
            nextBitmap = nextBitmap != null ? nextBitmap : createNewBitmap(w, h);
            cache = nextBitmap;
        }
        return cache;
    }

    private Bitmap createNewBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
    }

    private void clearBitmapCache() {
        if (null != previousBitmap) {
            previousBitmap.recycle();
            previousBitmap = null;
        }
        if (null != currentBitmap) {
            currentBitmap.recycle();
            currentBitmap = null;
        }
        if (null != nextBitmap) {
            nextBitmap.recycle();
            nextBitmap = null;
        }
    }

}
