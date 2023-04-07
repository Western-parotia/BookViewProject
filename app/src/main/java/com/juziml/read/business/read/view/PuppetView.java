package com.juziml.read.business.read.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.juziml.read.business.read.anim.AnimHelper;
import com.juziml.read.business.read.anim.CoverAnimationEffecter;
import com.juziml.read.business.read.anim.IAnimationEffecter;
import com.juziml.read.business.read.anim.SimulationAnimationEffecter;


/**
 * 此View的作用就像幕后一样，负责接受事件并传递到动画，Effecter
 * create by zhusw on 2020-08-14 17:37
 */
public class PuppetView extends View implements EventProxy, AnimParentView {

    IAnimationEffecter animationEffect;
    AnimParentView parentView;
    private Bitmap previousViewBitmap;
    private Bitmap currentViewBitmap;
    private Bitmap nextViewBitmap;
    boolean performDrawCurlTexture = false;
    private int vWidth, vHeight;

    public PuppetView(@NonNull Context context) {
        this(context, null);
    }

    public PuppetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuppetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*
            view 可以单独关，但是不能单独打开硬件加速

            关闭硬件加速 卡到爆炸
            开启硬件加速，诱发 OpenGLRenderer: Path too large to be rendered into a texture
         */
//        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    public boolean animRunningOrTouching() {
        boolean animRunningOrTouching = false;
        if (null != animationEffect) {
            animRunningOrTouching = animRunning();
        }
        return animRunningOrTouching;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent viewParent = getParent();
        parentView = (AnimParentView) viewParent;
        if (null != animationEffect) {
            animationEffect.onViewAttachedToWindow();
        }
    }


    public void setAnimMode(int animMode) {
        //重置某些属性 与变量
        animationEffect = null;
        if (animMode == ReadLayoutManager.BookFlipMode.MODE_COVER) {
            animationEffect = new CoverAnimationEffecter(this);
        } else if (animMode == ReadLayoutManager.BookFlipMode.MODE_CURL) {
            animationEffect = new SimulationAnimationEffecter(this);
        }
        if (null != animationEffect) {
            animationEffect.onViewSizeChanged(vWidth, vHeight);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (performDrawCurlTexture && null != animationEffect) {
            animationEffect.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != animationEffect) {
            animationEffect.onViewDetachedFromWindow();
        }
    }

    /**
     * 这个会被调用多次，最终宽度为实际测量宽度-2px
     * 这样在 layoutmanager 进行布局时 才可以同时保持3个item被显示
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(5, heightMeasureSpec);
        int width = measureSize(5, widthMeasureSpec) - 2;
        setMeasuredDimension(width - 2, height);
        vWidth = width;
        vHeight = height;
        if (null != animationEffect) {
            animationEffect.onViewSizeChanged(vWidth, vHeight);
        }
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


    public void buildBitmap(int slideDirection) {
        performDrawCurlTexture = false;
        if (slideDirection == AnimHelper.SLID_DIRECTION_LEFT) {
            currentViewBitmap = parentView.getCurrentBitmap();
            nextViewBitmap = parentView.getNextBitmap();
        } else if (slideDirection == AnimHelper.SLID_DIRECTION_RIGHT) {
            previousViewBitmap = parentView.getPreviousBitmap();
        }
        performDrawCurlTexture = true;


    }


    @Override
    public boolean onItemViewTouchEvent(MotionEvent event) {
        if (null != animationEffect) {
            animationEffect.handlerEvent(event);
        }
        return true;
    }

    @Override
    public boolean animRunning() {
        if (null != animationEffect) {
            return animationEffect.animInEffect();
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (null != animationEffect) {
            animationEffect.onScroll();
        }
    }

    @Override
    public void onExpectNext() {
        parentView.onExpectNext();
    }

    @Override
    public void onExpectPrevious() {
        parentView.onExpectPrevious();
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return previousViewBitmap;
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return currentViewBitmap;
    }

    @Override
    public Bitmap getNextBitmap() {
        return nextViewBitmap;
    }

    @Override
    public int getBackgroundColor() {
        return parentView.getBackgroundColor();
    }

    @Override
    public AnimHelper getAnimHelper() {
        return parentView.getAnimHelper();
    }

    @Override
    public void onClickMenuArea() {
        parentView.onClickMenuArea();
    }

    @Override
    public void onClickNextArea() {
        parentView.onClickNextArea();
    }

    @Override
    public void onClickPreviousArea() {
        parentView.onClickPreviousArea();
    }

    public void reset() {
        previousViewBitmap = null;
        nextViewBitmap = null;
        currentViewBitmap = null;
        performDrawCurlTexture = false;
    }

}
