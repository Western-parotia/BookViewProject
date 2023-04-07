package com.juziml.read.business.read.anim;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * create by zhusw on 2020-08-24 14:05
 */
public interface IAnimationEffecter {

    void onScroll();

    void handlerEvent(MotionEvent event);

    void draw(Canvas canvas);

    boolean animInEffect();

    void onViewSizeChanged(int vWidth, int vHeight);

    void onViewAttachedToWindow();

    void onViewDetachedFromWindow();
}
