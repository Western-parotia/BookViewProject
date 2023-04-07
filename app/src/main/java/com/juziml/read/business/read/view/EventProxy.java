package com.juziml.read.business.read.view;

import android.view.MotionEvent;

/**
 * create by zhusw on 2020-08-14 18:37
 */
public interface EventProxy {
    boolean onItemViewTouchEvent(MotionEvent event);

    boolean animRunning();
}
