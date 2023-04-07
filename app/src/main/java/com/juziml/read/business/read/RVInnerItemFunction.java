package com.juziml.read.business.read;

import android.view.MotionEvent;

/**
 * -recyclerview 内部功能接口
 * create by zhusw on 2020-08-17 10:07
 */
public interface RVInnerItemFunction {
    void onItemViewTouchEvent(MotionEvent event);

    boolean animRunning();

    void onClickMenu();

}
