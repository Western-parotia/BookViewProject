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
 * -关闭抛投效果
 * create by zhusw on 2020-03-30 11:51
 */
public class ReadRecyclerView extends RecyclerView implements RVInnerItemFunction, RVOuterFunction {

    private final ReadLayoutManager readLayoutManger;

    private boolean allowInterceptTouchEvent = true;

    private int currentPosition = 0;
    private WeakReference<EventProxy> eventProxyWeakReference;
    private AnimParentView animParentView;
    private BookView.OnPositionChangedListener onPositionChangedListener;

    public ReadRecyclerView(Context context) {
        this(context, null);
    }

    public ReadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readLayoutManger = new ReadLayoutManager(context);
        setLayoutManager(readLayoutManger);
        readLayoutManger.setOnForceLayoutCompleted(new ItemOnForceLayoutCompleted());
        readLayoutManger.setonStopScroller(new ItemOnScrollStop());
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
        readLayoutManger.forceScrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        readLayoutManger.smoothScrollToPosition(position);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        readLayoutManger.onRecyclerViewSizeChange();
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

                if (moveSampling.size() == 0
                        || mx != moveSampling.get(moveSampling.size() - 1)) {
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
                        readLayoutManger.setAutoLeftScroll(finallyMoveX < 10);
                    } else {
                        readLayoutManger.setAutoLeftScroll(finallyMoveX < 0);
                    }
                    moveSampling.clear();
                } else {
                    readLayoutManger.setAutoLeftScroll(false);
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //直接设置为false ，相当于所有子view的滑动 recyclerview 都不再拦截 一劳永逸
        if (allowInterceptTouchEvent) {
            return super.onInterceptTouchEvent(e);
        }
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
        readLayoutManger.setBookFlipMode(flipMode);
        if (flipMode == ReadLayoutManager.BookFlipMode.MODE_CURL
                || flipMode == ReadLayoutManager.BookFlipMode.MODE_COVER) {
            allowInterceptTouchEvent = false;
        } else {
            allowInterceptTouchEvent = true;
        }
        readLayoutManger.requestLayout();
    }

    @Override
    public int getFlipMode() {
        return readLayoutManger.getBookFlipMode();
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

    private class ItemOnScrollStop implements ReadLayoutManager.OnStopScroller {
        @Override
        public void onStop(boolean autoLeftScroll, int curPos) {
            boolean arriveNext = currentPosition < curPos;
            currentPosition = curPos;
            if (null != onPositionChangedListener) {
                onPositionChangedListener.onChanged(arriveNext, curPos);
            }
        }

    }

    private class ItemOnForceLayoutCompleted implements ReadLayoutManager.OnForceLayoutCompleted {

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
            pb = printViewToBitmap(prePos);
        }
        return pb;
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return printViewToBitmap(currentPosition);
    }

    @Override
    public Bitmap getNextBitmap() {
        final int dataCount = getAdapter().getItemCount();
        int nextPos = currentPosition + 1;
        Bitmap nb = null;
        if (nextPos < dataCount) {
            nb = printViewToBitmap(nextPos);
        }
        return nb;
    }

    /**
     * 将view渲染结果 打印到一个bitmap上
     *
     * @param pos
     * @return
     */
    private Bitmap printViewToBitmap(int pos) {
        View view = readLayoutManger.findViewByPosition(pos);
        if (null != view) {
            if (view instanceof PagerLayout) {
                PagerLayout pageView = (PagerLayout) view;
                Bitmap bitmapTarget = Bitmap.createBitmap(pageView.getWidth(), pageView.getHeight(), Bitmap.Config.ARGB_4444);
                pageView.drawViewScreenShotToBitmap(bitmapTarget);
                return bitmapTarget;
            } else {
                throw new IllegalArgumentException("item root view must be us Curl2dAnimViewGroup");
            }
        }
        return null;
    }


}
