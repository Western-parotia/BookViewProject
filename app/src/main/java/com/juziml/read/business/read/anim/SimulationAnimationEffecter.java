package com.juziml.read.business.read.anim;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.juziml.read.business.read.view.FPoint;
import com.juziml.read.business.read.view.PuppetView;

import java.util.LinkedList;
import java.util.List;

/**
 * create by zhusw on 2020-08-24 14:06
 */
public class SimulationAnimationEffecter implements IAnimationEffecter {
    private final static int DOWN_AREA_TOP_RIGHT = 1;
    private final static int DOWN_AREA_BOTTOM_RIGHT = 2;
    private final static int DOWN_AREA_LEFT = 3;
    private final static int DOWN_AREA_CENTRE_RIGHT = 4;
    private final static int DOWN_AREA_NONE = 5;
    private final static int DOWN_AREA_MENU = 6;


    int vWidth = 1;
    int vHeight = 1;
    private final PuppetView puppetView;
    private boolean isCancelFlip = false;
    private boolean curlAnimationRunning = false;
    private boolean isTouching = false;
    private final Scroller scroller;

    Paint pointPaint;

    Paint pathPaint;
    Paint bitmapPaint;
    Path pathA;
    Path pathC;
    Path pathRightShadow;
    Path pathLeftShadow;
    FPoint a, f, g, e, h, c, j, b, k, d, i;

    FPoint g2, e2;


    private final float[] matrixArray = {0, 0, 0, 0, 0, 0, 0, 0, 1.0f};
    private final Matrix matrix = new Matrix();

    float lPathAShadowDis = 0F;
    float rPathAShadowDis = 0F;
    private final RectF menuBounds = new RectF();

    private final ColorMatrixColorFilter colorMatrixColorFilter;

    private final ScrollRunnable scrollRunnable = new ScrollRunnable();


    public SimulationAnimationEffecter(PuppetView readAnimView) {
        this.puppetView = readAnimView;
        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setTextSize(25);

        a = new FPoint(-1, -1);
        f = new FPoint();
        g = new FPoint();
        e = new FPoint();
        h = new FPoint();
        c = new FPoint();
        j = new FPoint();
        b = new FPoint();
        k = new FPoint();
        d = new FPoint();
        i = new FPoint();

        g2 = new FPoint();
        e2 = new FPoint();

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathA = new Path();
        pathC = new Path();
        pathRightShadow = new Path();
        pathLeftShadow = new Path();

        ColorMatrix cm = new ColorMatrix();
        float[] array = {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
        cm.set(array);
        colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        scroller = new Scroller(readAnimView.getContext(), new LinearInterpolator());

    }


    @Override
    public void onScroll() {
        if (scroller.computeScrollOffset()) {
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            int finalX = scroller.getFinalX();
            int finalY = scroller.getFinalY();
            if (x == finalX && y == finalY) {
                if (!isCancelFlip) {
                    if (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
                        puppetView.onExpectNext();
                    } else if (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT) {
                        puppetView.onExpectPrevious();
                    }
                }
                puppetView.post(scrollRunnable);
            } else {
                touchMove(x, y, curlSlideDirection, false, true);
            }
        }

    }

    @Override
    public void handlerEvent(MotionEvent event) {
        if (curlAnimationRunning) return;
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                drawCurlAnimBefore = false;
                moveSampling.clear();
                downX = x;
                curlSlideDirection = AnimHelper.SLID_DIRECTION_UNKNOWN;
                if (x < menuBounds.left) {
                    downArea = DOWN_AREA_LEFT;
                } else if (x > menuBounds.left && y < menuBounds.top) {
                    downArea = DOWN_AREA_TOP_RIGHT;
                } else if (x > menuBounds.left && y > menuBounds.bottom) {
                    downArea = DOWN_AREA_BOTTOM_RIGHT;
                } else if (x > menuBounds.right) {
                    downArea = DOWN_AREA_CENTRE_RIGHT;
                } else if (x > menuBounds.left && y > menuBounds.top && x < menuBounds.right && y < menuBounds.bottom) {
                    downArea = DOWN_AREA_MENU;
                } else {
                    downArea = DOWN_AREA_NONE;
                }
                break;
            case MotionEvent.ACTION_MOVE://确定滑动方向
                isTouching = true;
                if (checkDownArea(downArea)) {
                    float moveDistance = x - downX;
                    //滑动距离超过5px，且单次事件周期只设置一个方向，首次滑动距离大于5px时为方向判断依据
                    if (downArea != DOWN_AREA_MENU && Math.abs(moveDistance) > AnimHelper.MOVE_SLOP && curlSlideDirection == AnimHelper.SLID_DIRECTION_UNKNOWN) {
                        if (moveDistance > 0) {
                            curlSlideDirection = AnimHelper.SLID_DIRECTION_RIGHT;
                        } else {
                            curlSlideDirection = AnimHelper.SLID_DIRECTION_LEFT;
                        }
                        touchDown(downArea, curlSlideDirection);
                        puppetView.buildBitmap(curlSlideDirection);
                        drawCurlAnimBefore = checkAnimCondition(curlSlideDirection, downArea);
                    }
                    if (drawCurlAnimBefore) {
                        if (moveSampling.size() == 0 || x != moveSampling.get(moveSampling.size() - 1)) {
                            moveSampling.add(x);
                        }
                        if (moveSampling.size() > MAX_COUNT) {
                            moveSampling.remove(0);
                        }
                        touchMoveAndInvalidate(x, y, curlSlideDirection, true);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL://[避免curl动画执行时，切换了flipMode 响应ACTION_CANCEL]
                isTouching = false;
                break;
            case MotionEvent.ACTION_UP://需要对最后手势进行采样，判断是 取消还是自动翻页
                if (drawCurlAnimBefore) {
                    if (moveSampling.size() > 0) {
                        float lastMoveX = moveSampling.get(moveSampling.size() - 1);
                        float firstMoveX = moveSampling.get(0);
                        float finallyMoveX = lastMoveX - firstMoveX;
                        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
                            boolean lastFingerLeftSlop = finallyMoveX < 10;
                            touchUp(lastFingerLeftSlop);
                        } else if (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT) {
                            finallyMoveX = lastMoveX - firstMoveX;

                            touchUp(finallyMoveX < 0);
                        }
                        moveSampling.clear();
                    } else {
                        touchUp(false);
                    }
                } else if (downArea == DOWN_AREA_MENU) {
                    if (x > menuBounds.left && x < menuBounds.right && y > menuBounds.top && y < menuBounds.bottom) {
                        puppetView.onClickMenuArea();
                    }
                } else if (downArea != DOWN_AREA_NONE) {

                    if (x == downX && downX >= vWidth / 2F) {//下一页
                        curlSlideDirection = AnimHelper.SLID_DIRECTION_LEFT;
                        touchDown(downArea, curlSlideDirection);
                        puppetView.buildBitmap(curlSlideDirection);
                        if (checkAnimCondition(curlSlideDirection, downArea)) {
                            touchMove(x, y, curlSlideDirection, true, false);
                            touchUp(true);
                        }

                    } else if (x == downX && downX < vWidth / 2F) {//上一页
                        curlSlideDirection = AnimHelper.SLID_DIRECTION_RIGHT;
                        touchDown(downArea, curlSlideDirection);
                        puppetView.buildBitmap(curlSlideDirection);
                        if (checkAnimCondition(curlSlideDirection, downArea)) {
                            touchMove(x, y, curlSlideDirection, true, false);
                            touchUp(false);
                        }
                    }
                }
                isTouching = false;
                downX = 0F;
                drawCurlAnimBefore = false;
                break;
        }

    }


    @Override
    public void onViewSizeChanged(int vWidth, int vHeight) {
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        a.x = -1;
        a.y = -1;
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

    @Override
    public void draw(Canvas canvas) {
        if (a.x == -1 && a.y == -1) {
            return;
        }
        if (curlSlideDirection != AnimHelper.SLID_DIRECTION_LEFT && curlSlideDirection != AnimHelper.SLID_DIRECTION_RIGHT) {
            return;
        }
        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT && (null == puppetView.getCurrentBitmap() || null == puppetView.getNextBitmap())) {
            return;
        }
        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT && null == puppetView.getPreviousBitmap()) {
            return;
        }

        Path pathA;
        if (f.x == vWidth && f.y == 0) {
            pathA = getPathAFromRightTop();
        } else {
            pathA = getPathAFromRightBottom();
        }
        Path pathC = getPathC();
        drawContent(canvas, pathA, pathC, puppetView.getCurrentBitmap(), puppetView.getNextBitmap(), puppetView.getPreviousBitmap());
        drawShaDow(canvas, pathA, pathC);
    }

    @Override
    public boolean animInEffect() {
        return curlAnimationRunning || isTouching;
    }

    /**
     * @param lastFingerLeftSlop true:最后手指左倾斜 ，false：最后手指右倾斜
     */
    public void touchUp(final boolean lastFingerLeftSlop) {

        int ax = (int) a.x;
        int ay = (int) a.y;
        boolean isCancelFlip;
        int dx;
        int dy;
        if (lastFingerLeftSlop) {
            dx = -vWidth - ax;
        } else {
            dx = vWidth - ax;
        }
        isCancelFlip = (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT && lastFingerLeftSlop) || (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT && !lastFingerLeftSlop);

        if (downArea == DOWN_AREA_TOP_RIGHT) {
            dy = -ay;
        } else {
            dy = (vHeight - ay);
        }
        this.isCancelFlip = isCancelFlip;
        int duration = isCancelFlip ? AnimHelper.CANCEL_ANIM_DURATION : AnimHelper.RELAY_ANIM_DURATION;
        curlAnimationRunning = true;
        scroller.startScroll(ax, ay, dx, dy, duration);
        invalidate();//猛然想起startScroll 需要在下一帧重绘时才生效
    }


    private int downArea = DOWN_AREA_NONE;
    private float downX = 0F;
    private int curlSlideDirection = AnimHelper.SLID_DIRECTION_UNKNOWN;
    private final List<Float> moveSampling = new LinkedList<>();
    private final int MAX_COUNT = 5;
    private boolean drawCurlAnimBefore = false;


    private boolean checkDownArea(int downArea) {

        return downArea != DOWN_AREA_MENU && downArea != DOWN_AREA_NONE;
    }

    private boolean checkAnimCondition(int curlSlideDirection, int downArea) {

        boolean notAtSlideArea = !checkDownArea(downArea);

        if (curlAnimationRunning || notAtSlideArea) return false;

        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT && (null != puppetView.getNextBitmap() && null != puppetView.getCurrentBitmap())) {
            return true;
        } else if (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT && null != puppetView.getPreviousBitmap()) {
            return true;
        }
        return false;
    }

    public void touchDown(int downArea, int curlSlideDirection) {
        //判断触摸起始点位置，确定f点
        if (downArea == DOWN_AREA_TOP_RIGHT && curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            f.x = vWidth;
            f.y = 0;
        } else {
            f.x = vWidth;
            f.y = vHeight;
        }

    }


    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @param x
     * @param y
     * @param curlSlideDirection
     * @param offset             C.x <0 自动修正时A点坐标 避免a点不动
     * @param invalidate
     */
    private void touchMove(float x, float y, int curlSlideDirection, boolean offset, boolean invalidate) {
        a.x = x;
        final float minGap = dpToPx(5F);
        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_RIGHT || (downArea == DOWN_AREA_LEFT || downArea == DOWN_AREA_CENTRE_RIGHT)) {
            a.y = vHeight - minGap;
        } else {
            a.y = y;
        }
        calcPointsXY(a, f);
        if (offset) {
            //修正c点范围 不可小于0
            if (calcPointCX(f) < 0) {
                calcPointAByTouchPoint();
                calcPointsXY(a, f);
            }
        }
        if (invalidate) {
            invalidate();
        }
    }

    private void invalidate() {
        puppetView.postInvalidate();
    }

    private void touchMoveAndInvalidate(float x, float y, int curlSlideDirection, boolean offset) {
        touchMove(x, y, curlSlideDirection, offset, true);
    }


    private void drawShaDow(Canvas canvas, Path pathA, Path pathC) {
        drawPathARightShadow(canvas, pathA);
        drawPathALeftShadow(canvas, pathA);
        drawPathBShadow(canvas, pathA, pathC);
        drawPathCShadow(canvas, pathA);

    }

    private void drawContent(Canvas canvas, Path pathA, Path pathC, Bitmap curBitmap, Bitmap nextBitmap, Bitmap preBitmap) {

        //B图层就是底图，也就是 backgroundBitmap 即可
        float eh = (float) Math.hypot(f.x - e.x, h.y - f.y);
        float sin0 = (f.x - e.x) / eh;
        float cos0 = (h.y - f.y) / eh;
        //设置图形翻转和旋转矩阵
        matrixArray[0] = -(1 - 2 * sin0 * sin0);
        matrixArray[1] = 2 * sin0 * cos0;
        matrixArray[3] = 2 * sin0 * cos0;
        matrixArray[4] = 1 - 2 * sin0 * sin0;
        matrix.reset();
        matrix.setValues(matrixArray);
        matrix.preTranslate(-e.x, -e.y);
        matrix.postTranslate(e.x, e.y);


        if (curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {

            //对Bitmap进行取色
            int color = curBitmap.getPixel(1, 1);
            //获取对应的三色
            int red = (color & 0xff0000) >> 16;
            int green = (color & 0x00ff00) >> 8;
            int blue = (color & 0x0000ff);
            //转换成含有透明度的颜色
            int tempColor = Color.argb(200, red, green, blue);

            canvas.save();
            canvas.clipPath(pathC);
            canvas.clipPath(pathA, Region.Op.DIFFERENCE);
            canvas.drawColor(puppetView.getBackgroundColor());
            bitmapPaint.setColorFilter(colorMatrixColorFilter);
            canvas.drawBitmap(curBitmap, matrix, bitmapPaint);//绘制背面到C区
            canvas.drawColor(tempColor);//叠加背景
            bitmapPaint.setColorFilter(null);

            canvas.restore();

            canvas.save();
            canvas.clipPath(pathC, Region.Op.DIFFERENCE);
            canvas.clipPath(pathA, Region.Op.DIFFERENCE);
            canvas.drawBitmap(nextBitmap, 0, 0, bitmapPaint);//绘制B区
            canvas.restore();
        } else {
            //对Bitmap进行取色
            int color = preBitmap.getPixel(1, 1);
            //获取对应的三色
            int red = (color & 0xff0000) >> 16;
            int green = (color & 0x00ff00) >> 8;
            int blue = (color & 0x0000ff);
            //转换成含有透明度的颜色
            int tempColor = Color.argb(200, red, green, blue);

            bitmapPaint.setColorFilter(colorMatrixColorFilter);
            canvas.save();
            canvas.clipPath(pathC);
            canvas.clipPath(pathA, Region.Op.DIFFERENCE);
            bitmapPaint.setColorFilter(colorMatrixColorFilter);
            canvas.drawBitmap(preBitmap, matrix, bitmapPaint);//绘制背面到C区
            canvas.drawColor(tempColor);//叠加背景
            bitmapPaint.setColorFilter(null);
            canvas.restore();

            canvas.save();
            canvas.clipPath(pathA);//绘制正面到A区
            canvas.drawBitmap(preBitmap, 0, 0, bitmapPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制B区域阴影，阴影左深右浅
     *
     * @param canvas
     */
    private void drawPathBShadow(Canvas canvas, Path pathA, Path pathC) {
        int deepOffset = 0;//深色端的偏移值
        int lightOffset = 0;//浅色端的偏移值
        float aTof = (float) Math.hypot((a.x - f.x), (a.y - f.y));//a到f的距离
        float viewDiagonalLength = (float) Math.hypot(vWidth, vHeight);//对角线长度

        int left;
        int right;
        int top = (int) c.y;
        int bottom = (int) (viewDiagonalLength + c.y);
        GradientDrawable gradientDrawable;
        if (downArea == DOWN_AREA_TOP_RIGHT && curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            //从左向右线性渐变
            gradientDrawable = puppetView.getAnimHelper().getTopBGradientDrawable();

            left = (int) (c.x - deepOffset);//c点位于左上角
            right = (int) (c.x + aTof / 4 + lightOffset);
        } else {
            //从右向左线性渐变
            gradientDrawable = puppetView.getAnimHelper().getBottomBGradientDrawable();

            left = (int) (c.x - aTof / 4 - lightOffset);//c点位于左下角
            right = (int) (c.x + deepOffset);
        }
        gradientDrawable.setBounds(left, top, right, bottom);//设置阴影矩形
        canvas.save();
        canvas.clipPath(pathA, Region.Op.DIFFERENCE);
        canvas.clipPath(pathC, Region.Op.DIFFERENCE);//留出b区

        float rotateDegrees = (float) Math.toDegrees(Math.atan2(e.x - f.x, h.y - f.y));//旋转角度
        canvas.rotate(rotateDegrees, c.x, c.y);//以c为中心点旋转
        gradientDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制C区域阴影，阴影左浅右深
     *
     * @param canvas
     */
    private void drawPathCShadow(Canvas canvas, Path pathA) {
        int deepOffset = 1;//深色端的偏移值
        int lightOffset = 0;//浅色端的偏移值
        float viewDiagonalLength = (float) Math.hypot(vWidth, vHeight);//view对角线长度
        int midpoint_ce = (int) (c.x + e.x) / 2;//ce中点
        int midpoint_jh = (int) (j.y + h.y) / 2;//jh中点
        float minDisToControlPoint = Math.min(Math.abs(midpoint_ce - e.x), Math.abs(midpoint_jh - h.y));//中点到控制点的最小值

        int left;
        int right;
        int top = (int) c.y;
        int bottom = (int) (viewDiagonalLength + c.y);
        GradientDrawable gradientDrawable;
        if (downArea == DOWN_AREA_TOP_RIGHT && curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            gradientDrawable = puppetView.getAnimHelper().getTopCGradientDrawable();
            left = (int) (c.x - lightOffset);
            right = (int) (c.x + minDisToControlPoint + deepOffset);
        } else {
            gradientDrawable = puppetView.getAnimHelper().getBottomCGradientDrawable();
            left = (int) (c.x - minDisToControlPoint - deepOffset);
            right = (int) (c.x + lightOffset);
        }
        gradientDrawable.setBounds(left, top, right, bottom);
        canvas.save();
        canvas.clipPath(pathC);
        canvas.clipPath(pathA, Region.Op.DIFFERENCE);//期望的C区
        float mDegrees = (float) Math.toDegrees(Math.atan2(e.x - f.x, h.y - f.y));
        canvas.rotate(mDegrees, c.x, c.y);
        gradientDrawable.draw(canvas);

        canvas.restore();

    }

    /**
     * 绘制A区域左阴影
     *
     * @param canvas
     */
    private void drawPathALeftShadow(Canvas canvas, Path pathA) {

        int left;
        int right;
        int top = (int) e.y;
        int bottom = (int) (e.y + vHeight);
        GradientDrawable gradientDrawable;
        if (downArea == DOWN_AREA_TOP_RIGHT && curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            gradientDrawable = puppetView.getAnimHelper().getTopLeftGradientDrawable();
            left = (int) (e.x - lPathAShadowDis / 2);
            right = (int) (e.x);
        } else {
            gradientDrawable = puppetView.getAnimHelper().getBottomLeftGradientDrawable();
            left = (int) (e.x);
            right = (int) (e.x + lPathAShadowDis / 2);
        }
        //裁剪出我们需要的区域
        pathLeftShadow.reset();
        pathLeftShadow.moveTo(a.x - Math.max(rPathAShadowDis, lPathAShadowDis) / 2, a.y);
        pathLeftShadow.lineTo(d.x, d.y);
        pathLeftShadow.lineTo(e.x, e.y);
        pathLeftShadow.lineTo(a.x, a.y);
        pathLeftShadow.close();
        canvas.save();

        canvas.clipPath(pathA);
        canvas.clipPath(pathLeftShadow, Region.Op.INTERSECT);
        float mDegrees = (float) Math.toDegrees(Math.atan2(e.x - a.x, a.y - e.y));
        canvas.rotate(mDegrees, e.x, e.y);
        gradientDrawable.setBounds(left, top, right, bottom);
        gradientDrawable.draw(canvas);
        canvas.restore();

    }

    /**
     * 绘制A区域右阴影
     *
     * @param canvas
     */
    private void drawPathARightShadow(Canvas canvas, Path pathA) {

        float viewDiagonalLength = (float) Math.hypot(vWidth, vHeight);//view对角线长度
        int left = (int) h.x;
        int right = (int) (h.x + viewDiagonalLength * 10);//需要足够长的长度
        int top;
        int bottom;
        int offset = 0;
        GradientDrawable gradientDrawable;
        if (downArea == DOWN_AREA_TOP_RIGHT && curlSlideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            gradientDrawable = puppetView.getAnimHelper().getTopRightGradientDrawable();
            top = (int) (h.y - rPathAShadowDis / 2) + offset;
            bottom = (int) h.y;
        } else {
            gradientDrawable = puppetView.getAnimHelper().getBottomRightGradientDrawable();
            top = (int) h.y;
            bottom = (int) (h.y + rPathAShadowDis / 2);
        }
        gradientDrawable.setBounds(left, top, right, bottom);

        //裁剪出我们需要的区域
        pathRightShadow.reset();
        pathRightShadow.moveTo(a.x - Math.max(rPathAShadowDis, lPathAShadowDis) / 2, a.y);
        pathRightShadow.lineTo(h.x, h.y);
        pathRightShadow.lineTo(a.x, a.y);
        pathRightShadow.close();
        canvas.save();
        canvas.clipPath(pathA);
        canvas.clipPath(pathRightShadow);
        float mDegrees = (float) Math.toDegrees(Math.atan2(a.y - h.y, a.x - h.x));
        canvas.rotate(mDegrees, h.x, h.y);
        gradientDrawable.draw(canvas);
        canvas.restore();

    }

    /**
     * 触发区域 右下
     *
     * @return
     */
    private Path getPathAFromRightBottom() {
        pathA.reset();
        pathA.lineTo(0, vHeight);//移动到左下角
        pathA.lineTo(c.x, c.y);//移动到c点
        pathA.quadTo(e.x, e.y, b.x, b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x, a.y);//移动到a点
        pathA.lineTo(k.x, k.y);//移动到k点
        pathA.quadTo(h.x, h.y, j.x, j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(vWidth, 0);//移动到右上角
        pathA.close();//闭合区域
        return pathA;
    }

    /**
     * 触发区域 右上
     *
     * @return
     */
    private Path getPathAFromRightTop() {
        pathA.reset();
        pathA.moveTo(0, 0);
        pathA.lineTo(c.x, c.y);//移动到c点
        pathA.quadTo(e.x, e.y, b.x, b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x, a.y);//移动到a点
        pathA.lineTo(k.x, k.y);//移动到k点
        pathA.quadTo(h.x, h.y, j.x, j.y);//从k到j画贝塞尔曲线，控制点为h
        pathA.lineTo(vWidth, vHeight);//移动到右下角
        pathA.lineTo(0, vHeight);//移动到左下角
        pathA.close();
        return pathA;
    }

    private Path getPathC() {
        pathC.reset();
        pathC.moveTo(d.x, d.y);
        pathC.lineTo(b.x, b.y);
        pathC.lineTo(a.x, a.y);
        pathC.lineTo(k.x, k.y);
        pathC.lineTo(i.x, i.y);
        pathC.close();
        return pathC;
    }

    /**
     * 仅用于确认点位时使用
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        calcPointsXY(a, f);
        canvas.drawText("a", a.x, a.y, pointPaint);

        canvas.drawText("f", f.x, f.y, pointPaint);

        canvas.drawText("g", g.x, g.y, pointPaint);

        canvas.drawText("e", e.x, e.y, pointPaint);
        canvas.drawText("h", h.x, h.y, pointPaint);

        canvas.drawText("c", c.x, c.y, pointPaint);
        canvas.drawText("j", j.x, j.y, pointPaint);

        canvas.drawText("b", b.x, b.y, pointPaint);
        canvas.drawText("k", k.x, k.y, pointPaint);

        canvas.drawText("d", d.x, d.y, pointPaint);
        canvas.drawText("i", i.x, i.y, pointPaint);


    }

    /**
     * 如果c点x坐标小于0,根据触摸点重新测量a点坐标
     */
    private void calcPointAByTouchPoint() {
        float w0 = vWidth - c.x;
        float w1 = Math.abs(f.x - a.x);
        float w2 = vWidth * w1 / w0;
        a.x = Math.abs(f.x - w2);
        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a.y = Math.abs(f.y - h2);
    }


    private float calcPointCX(FPoint f) {
        g2.setXY(0F, 0F);
        e2.setXY(0F, 0F);

        g2.x = (a.x + f.x) / 2;
        g2.y = (a.y + f.y) / 2;

        e2.x = g2.x - (f.y - g2.y) * (f.y - g2.y) / (f.x - g2.x);
        e2.y = f.y;

        return e2.x - (f.x - e2.x) / 2;
    }


    /**
     * 计算切点坐标，这里就没有推导公式了，直接拿来用了
     *
     * @param lineOne_My_pointOne
     * @param lineOne_My_pointTwo
     * @param lineTwo_My_pointOne
     * @param lineTwo_My_pointTwo
     * @return
     */
    private void calculateIntersectionPoint(FPoint save, FPoint lineOne_My_pointOne, FPoint lineOne_My_pointTwo, FPoint lineTwo_My_pointOne, FPoint lineTwo_My_pointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOne_My_pointOne.x;
        y1 = lineOne_My_pointOne.y;
        x2 = lineOne_My_pointTwo.x;
        y2 = lineOne_My_pointTwo.y;
        x3 = lineTwo_My_pointOne.x;
        y3 = lineTwo_My_pointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;

        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4)) / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        save.setXY(pointX, pointY);
    }


    /**
     * 计算各点坐标
     *
     * @param a
     * @param f
     */
    private void calcPointsXY(FPoint a, FPoint f) {
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;

        h.x = f.x;
        h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y);

        c.x = e.x - (f.x - e.x) / 2;
        c.y = f.y;

        j.x = f.x;
        j.y = h.y - (f.y - h.y) / 2;

        calculateIntersectionPoint(b, a, e, c, j);
        calculateIntersectionPoint(k, a, h, c, j);

        d.x = (c.x + 2 * e.x + b.x) / 4;
        d.y = (2 * e.y + c.y + b.y) / 4;

        i.x = (j.x + 2 * h.x + k.x) / 4;
        i.y = (2 * h.y + j.y + k.y) / 4;

        //计算d点到ae的距离
        float lA = a.y - e.y;
        float lB = e.x - a.x;
        float lC = a.x * e.y - e.x * a.y;
        lPathAShadowDis = Math.abs((lA * d.x + lB * d.y + lC) / (float) Math.hypot(lA, lB));

        //计算i点到ah的距离
        float rA = a.y - h.y;
        float rB = h.x - a.x;
        float rC = a.x * h.y - h.x * a.y;
        rPathAShadowDis = Math.abs((rA * i.x + rB * i.y + rC) / (float) Math.hypot(rA, rB));
    }

    protected class ScrollRunnable implements Runnable {
        @Override
        public void run() {
            puppetView.reset();
            curlAnimationRunning = false;
            invalidate();
        }
    }
}
