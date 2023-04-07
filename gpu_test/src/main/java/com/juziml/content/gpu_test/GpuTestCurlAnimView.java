package com.juziml.content.gpu_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * create by zhusw on 2020-07-29 10:54
 */
public class GpuTestCurlAnimView extends View {

    Paint pointPaint;
    FPoint a, f, g, e, h, c, j, b, k, d, i;

    int width;
    int height;

    //图形
    Paint pathAPaint;
    Path pathA;
    Bitmap holderBitmap;
    Canvas bitmapCanvas = new Canvas();

    Paint pathCPaint;
    Path pathC;
    Paint pathBPaint;
    Path pathB;
    PorterDuffXfermode xfDST_ATOP = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);

    public GpuTestCurlAnimView(Context context) {
        this(context, null);
    }


    public GpuTestCurlAnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        a.setXY(-1, -1);
        holderBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    }

    public GpuTestCurlAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setTextSize(25);
        a = new FPoint();
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


        pathAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathAPaint.setColor(Color.GREEN);
        pathA = new Path();

        pathCPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathCPaint.setColor(Color.YELLOW);
        pathCPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));//消除c区 与a区重叠部分,原文注释：丢弃原图想覆盖目标图像的区域
        pathC = new Path();

        pathBPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathBPaint.setColor(Color.BLUE);
        pathB = new Path();
    }

    public void flipPrepare(float x, float y) {
        if (y < height / 2) {
            f.setXY(width, 0);
        } else {
            f.setXY(width, height);
        }
    }

    public void flipSetToDefault() {
        a.setXY(-1, -1);
        f.setXY(0, 0);
        calculatePoinXY(a, f);
        postInvalidate();
    }

    public void flipCurl(float x, float y) {
        a.x = x;
        a.y = y;
        calculatePoinXY(a, f);

        //修正c点范围 不可小于0
        if (calculateCxRange(a.x, a.y, f) < 0) {
            calcPointAByTouchPoint();
            calculatePoinXY(a, f);
        }
        postInvalidate();
    }

    private float calculateCxRange(float ax, float ay, FPoint f) {
        float gx = (ax + f.x) / 2;
        float gy = (ay + f.y) / 2;
        float ex = gx - (f.y - gy) * (f.y - gy) / (f.x - gx);
        return ex - (f.x - ex) / 2;
    }

    /**
     * 如果c点x坐标小于0,根据触摸点重新测量a点坐标
     */
    private void calcPointAByTouchPoint() {
        float w0 = width - c.x;
        float w1 = Math.abs(f.x - a.x);
        float w2 = width * w1 / w0;
        a.x = Math.abs(f.x - w2);
        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a.y = Math.abs(f.y - h2);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        //把点绘制在前景上 方便观察
        drawPoint(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (a.x == -1 && a.y == -1) {
            canvas.drawPath(getDefaultPath(), pathAPaint);
        } else {
            Path pathA = getPathAFromRightTop();
            if (f.x == width && f.y == height) {
                pathA = getPathAFromRightBottom();
            }
            Path pathC = getPathC();
            canvas.drawPath(pathC, pathCPaint);//先给C区画上底色
            canvas.drawPath(pathA, pathAPaint);//先给A区画上底色

            canvas.save();//扣出B区
            canvas.clipPath(pathC, Region.Op.DIFFERENCE);
            canvas.clipPath(pathA, Region.Op.DIFFERENCE);
            canvas.drawPath(getPathB(), pathBPaint);
            canvas.restore();
        }
    }

    public Path getDefaultPath() {
        pathA.reset();
        pathA.lineTo(0, height);
        pathA.lineTo(width, height);
        pathA.lineTo(width, 0);
        pathA.close();
        return pathA;
    }

    /**
     * 以右下角为触发起点
     *
     * @return
     */
    private Path getPathAFromRightBottom() {
        pathA.reset();
        //划线从 0，0开始
        pathA.lineTo(0, height);//划线到左下角
        pathA.lineTo(c.x, c.y);//划线到c点
        pathA.quadTo(e.x, e.y, b.x, b.y);//以c为起点，b为终点，e为动点 画曲线
        pathA.lineTo(a.x, a.y);
        pathA.lineTo(k.x, k.y);
        pathA.quadTo(h.x, h.y, j.x, j.y);//以k为起点，j为终点 h为动点 画曲线
        pathA.lineTo(width, 0);
        pathA.close();
        return pathA;
    }

    private Path getPathAFromRightTop() {
        pathA.reset();
        pathA.lineTo(c.x, c.y);//划线到c
        pathA.quadTo(e.x, e.y, b.x, b.y);//c-b的曲线
        pathA.lineTo(a.x, a.y);//划线到a
        pathA.lineTo(k.x, k.y);//划线到k
        pathA.quadTo(h.x, h.y, j.x, j.y);//k-j的曲线
        pathA.lineTo(width, height);
        pathA.lineTo(0, height);
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

    private Path getPathB() {
        pathB.reset();
        pathB.lineTo(0, height);
        pathB.lineTo(width, height);
        pathB.lineTo(width, 0);
        pathB.close();
        return pathB;
    }


    private void drawPoint(Canvas canvas) {
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
     * 计算切点坐标，这里就没有推导公式了，直接拿来用了
     *
     * @param lineOne_My_pointOne
     * @param lineOne_My_pointTwo
     * @param lineTwo_My_pointOne
     * @param lineTwo_My_pointTwo
     * @return
     */
    private FPoint getIntersectionPoint(FPoint lineOne_My_pointOne, FPoint lineOne_My_pointTwo, FPoint lineTwo_My_pointOne, FPoint lineTwo_My_pointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOne_My_pointOne.x;
        y1 = lineOne_My_pointOne.y;
        x2 = lineOne_My_pointTwo.x;
        y2 = lineOne_My_pointTwo.y;
        x3 = lineTwo_My_pointOne.x;
        y3 = lineTwo_My_pointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;

        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return new FPoint(pointX, pointY);
    }

    /**
     * 计算各点坐标
     */
    private void calculatePoinXY(FPoint a, FPoint f) {
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

        b = getIntersectionPoint(a, e, c, j);
        k = getIntersectionPoint(a, h, c, j);

        d.x = (c.x + 2 * e.x + b.x) / 4;
        d.y = (2 * e.y + c.y + b.y) / 4;

        i.x = (j.x + 2 * h.x + k.x) / 4;
        i.y = (2 * h.y + j.y + k.y) / 4;
    }

}
