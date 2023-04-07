package com.juziml.read.business.read.view;

import android.graphics.Bitmap;

import com.juziml.read.business.read.anim.AnimHelper;

/**
 * create by zhusw on 2020-08-03 11:28
 */
public interface AnimParentView {
    void onExpectNext();

    void onExpectPrevious();

    Bitmap getPreviousBitmap();

    Bitmap getCurrentBitmap();

    Bitmap getNextBitmap();

    int getBackgroundColor();

    AnimHelper getAnimHelper();

    void onClickMenuArea();

    /**
     * 只在非卷曲模式下调用
     */
    void onClickNextArea();

    /**
     * 只在非卷曲模式下调用
     */
    void onClickPreviousArea();

}
