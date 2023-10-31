package com.juziml.read.business.read.anim;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.juziml.read.business.read.view.PuppetView;
import com.juziml.read.utils.DLog;

import java.util.LinkedList;
import java.util.List;

/**
 * create by zhusw on 2020-08-24 14:06
 */
public class CoverAnimationEffecter implements IAnimationEffecter {
    private final static int DOWN_AREA_NONE = -1;
    private final static int DOWN_AREA_MENU = 1;
    private final static int DOWN_AREA_LEFT = 2;
    private final static int DOWN_AREA_RIGHT = 3;

    int vWidth = 1;
    int vHeight = 1;
    private final PuppetView puppetView;

    private boolean isCancelFlip = false;

    private boolean coverAnimationRunning = false;

    private boolean isTouching = false;

    private final Scroller scroller;
    private final ScrollRunnable scrollRunnable;
    private final RectF menuBounds;

    private final Path pathA;
    private final Path pathB;
    private final Paint paint;
    private final int shadowWidth;

    public CoverAnimationEffecter(PuppetView readAnimView) {
        this.puppetView = readAnimView;
        scroller = new Scroller(readAnimView.getContext(), new AccelerateDecelerateInterpolator());
        scrollRunnable = new ScrollRunnable();
        menuBounds = new RectF();
        pathA = new Path();
        pathB = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowWidth = 20;
    }

    private int downArea = DOWN_AREA_NONE;
    private float downX = 0F;
    private int coverSlideDirection = AnimHelper.SLID_DIRECTION_UNKNOWN;
    private final List<Float> moveSampling = new LinkedList<>();
    private final int MAX_COUNT = 5;
    private boolean prepareDrawCoverAnimEffect = false;

    private float currentX = -1;

    @Override
    public void handlerEvent(MotionEvent event) {
        if (coverAnimationRunning) return;
        float x = event.getRawX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveSampling.clear();
                downX = x;
                prepareDrawCoverAnimEffect = false;
                isTouching = true;
                currentX = -1;
                downArea = DOWN_AREA_NONE;
                coverSlideDirection = AnimHelper.SLID_DIRECTION_UNKNOWN;
                if (x > menuBounds.left && y > menuBounds.top
                        && x < menuBounds.right && y < menuBounds.bottom) {
                    downArea = DOWN_AREA_MENU;
                } else if (x < vWidth / 2F) {
                    downArea = DOWN_AREA_LEFT;
                } else {
                    downArea = DOWN_AREA_RIGHT;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isTouching = true;
                float curDistance = x - downX;
                if (coverSlideDirection == AnimHelper.SLID_DIRECTION_UNKNOWN && checkDownArea(downArea)) {
                    if (curDistance > 0) {
                        coverSlideDirection = AnimHelper.SLID_DIRECTION_RIGHT;
                    } else {
                        coverSlideDirection = AnimHelper.SLID_DIRECTION_LEFT;
                    }
                    puppetView.buildBitmap(coverSlideDirection);
                    prepareDrawCoverAnimEffect = checkAnimCondition(coverSlideDirection);
                }

                if (prepareDrawCoverAnimEffect) {
                    if (moveSampling.size() == 0
                            || x != moveSampling.get(moveSampling.size() - 1)) {
                        moveSampling.add(x);
                    }
                    if (moveSampling.size() > MAX_COUNT) {
                        moveSampling.remove(0);
                    }
                    currentX = x;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                break;
            case MotionEvent.ACTION_UP:
                currentX = x;
                if (prepareDrawCoverAnimEffect) {
                    if (moveSampling.size() > 0) {
                        float lastMoveX = moveSampling.get(moveSampling.size() - 1);
                        float firstMoveX = moveSampling.get(0);
                        float finallyMoveX = lastMoveX - firstMoveX;
                        if (coverSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
                            boolean lastFingerLeftSlop = finallyMoveX < 10;
                            touchUp(lastFingerLeftSlop);
                        } else if (coverSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT) {
                            finallyMoveX = lastMoveX - firstMoveX;
                            touchUp(finallyMoveX < 0);
                        }
                    } else {
                        touchUp(false);
                    }
                } else if (downArea == DOWN_AREA_MENU) {
                    if (x > menuBounds.left && x < menuBounds.right
                            && y > menuBounds.top && y < menuBounds.bottom) {
                        puppetView.onClickMenuArea();
                    }
                } else if (downArea != DOWN_AREA_NONE) {
                    if (x == downX && downX >= vWidth / 2F) {//下一页
                        coverSlideDirection = AnimHelper.SLID_DIRECTION_LEFT;
                        puppetView.buildBitmap(coverSlideDirection);
                        if (checkAnimCondition(coverSlideDirection)) {
                            touchUp(true);
                        }
                    } else if (x == downX && downX < vWidth / 2F) {//上一页
                        coverSlideDirection = AnimHelper.SLID_DIRECTION_RIGHT;
                        puppetView.buildBitmap(coverSlideDirection);
                        if (checkAnimCondition(coverSlideDirection)) {
                            touchUp(false);
                        }
                    }
                }
                moveSampling.clear();
                isTouching = false;
                break;
            default:
                break;
        }
    }

    private void touchUp(boolean lastFingerLeftSlop) {
        DLog.log("touchUp coverAnimationRunning=%s", coverAnimationRunning);
        coverAnimationRunning = true;
        isCancelFlip = (coverSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT && lastFingerLeftSlop)
                || (coverSlideDirection == AnimHelper.SLID_DIRECTION_LEFT && !lastFingerLeftSlop);

        int duration = isCancelFlip ? AnimHelper.CANCEL_ANIM_DURATION : AnimHelper.RELAY_ANIM_DURATION;
        duration = (int) (duration * 0.7F);
        int startX = (int) currentX;
        int startY = 0;
        int dy = 0;
        int dx;

        if (lastFingerLeftSlop) {

            dx = (int) -(vWidth - (downX - currentX));
        } else {
            dx = vWidth - (int) currentX;
        }
        scroller.startScroll(startX, startY, dx, dy, duration);
        invalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        if (currentX == -1) {
            DLog.log("CoverAnimationEffect draw 1");
            return;
        }
        if (coverSlideDirection != AnimHelper.SLID_DIRECTION_LEFT && coverSlideDirection != AnimHelper.SLID_DIRECTION_RIGHT) {
            DLog.log("CoverAnimationEffect draw 2");
            return;
        }
        if (coverSlideDirection == AnimHelper.SLID_DIRECTION_LEFT
                && (null == puppetView.getCurrentBitmap() || null == puppetView.getNextBitmap())) {
            DLog.log("CoverAnimationEffect draw 3");
            return;
        }
        if (coverSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT && null == puppetView.getPreviousBitmap()) {
            DLog.log("CoverAnimationEffect draw 4");
            return;
        }
        DLog.log("CoverAnimationEffect draw 5");
        if (coverSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            float offset = downX - currentX;
            offset = Math.max(0, offset);
            canvas.save();
            canvas.clipPath(getPathAToLeft());
            canvas.drawBitmap(puppetView.getCurrentBitmap(), -offset, 0, paint);
            canvas.restore();
            canvas.save();
            canvas.clipPath(getPathB());
            canvas.drawBitmap(puppetView.getNextBitmap(), 0, 0, paint);
            canvas.restore();
            drawShadow((int) (vWidth - offset), canvas);
        } else {
            float leftOffset = vWidth - currentX;
            canvas.save();
            canvas.clipPath(getPathAToRight());
            canvas.drawBitmap(puppetView.getPreviousBitmap(), -leftOffset, 0, paint);
            canvas.restore();
            drawShadow((int) currentX, canvas);
        }
    }

    private void drawShadow(int left, Canvas canvas) {
        GradientDrawable drawable = puppetView.getAnimHelper().getCoverGradientDrawable();
        drawable.setBounds(left, 0, left + shadowWidth, vHeight);
        drawable.draw(canvas);
    }

    private Path getPathAToLeft() {
        pathA.reset();
        float x = vWidth - (downX - currentX);
        x = Math.min(vWidth, x);
        pathA.lineTo(x, 0);
        pathA.lineTo(x, vHeight);
        pathA.lineTo(0, vHeight);
        pathA.close();
        return pathA;
    }

    private Path getPathB() {
        pathB.reset();
        float x = vWidth - (downX - currentX);
        x = Math.min(vWidth, x);
        pathB.moveTo(x, 0);
        pathB.lineTo(vWidth, 0);
        pathB.lineTo(vWidth, vHeight);
        pathB.lineTo(x, vHeight);
        pathB.close();
        return pathB;
    }

    private Path getPathAToRight() {
        pathA.reset();
        pathA.lineTo(currentX, 0);
        pathA.lineTo(currentX, vHeight);
        pathA.lineTo(0, vHeight);
        pathA.close();
        return pathA;
    }


    private boolean checkDownArea(int downArea) {
        return downArea != DOWN_AREA_MENU && downArea != DOWN_AREA_NONE;
    }

    private boolean checkAnimCondition(int slideDirection) {
        if (slideDirection == AnimHelper.SLID_DIRECTION_LEFT && (null != puppetView.getNextBitmap() && null != puppetView.getCurrentBitmap())) {
            return true;
        } else if (slideDirection == AnimHelper.SLID_DIRECTION_RIGHT && null != puppetView.getPreviousBitmap()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean animInEffect() {
        return isTouching || coverAnimationRunning;
    }

    @Override
    public void onViewSizeChanged(int vWidth, int vHeight) {
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        menuBounds.left = vWidth / 3F;
        menuBounds.top = vHeight / 3F;
        menuBounds.right = vWidth * 2 / 3F;
        menuBounds.bottom = vHeight * 2 / 3F;
    }

    @Override
    public void onViewAttachedToWindow() {

    }

    @Override
    public void onViewDetachedFromWindow() {
        puppetView.removeCallbacks(scrollRunnable);
    }

    private void invalidate() {
        puppetView.postInvalidate();
    }

    @Override
    public void onScroll() {
        if (scroller.computeScrollOffset()) {

            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            if (x == scroller.getFinalX() && y == scroller.getFinalY()) {
                scroller.forceFinished(true);
                //补一点时间，避免动画太快结束，提供两次动画触发间隔
                DLog.log("coverAnimationRunning coverAnimationRunning=%s 结束，延时开启 状态重置", coverAnimationRunning);
                puppetView.post(scrollRunnable);
            } else {
                currentX = x;
                invalidate();
            }
        }

    }


    protected class ScrollRunnable implements Runnable {
        @Override
        public void run() {
            puppetView.reset();
            coverAnimationRunning = false;
            if (!isCancelFlip) {
                if (coverSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
                    puppetView.onExpectNext();
                } else if (coverSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT) {
                    puppetView.onExpectPrevious();
                }
            }
            invalidate();
        }
    }
}
