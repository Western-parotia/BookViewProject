package com.juziml.read.business.read.anim;

import android.graphics.drawable.GradientDrawable;

/**
 * create by zhusw on 2020-08-06 15:53
 */
public final class AnimHelper {

    public final static float MOVE_SLOP = 1;

    public final static int SLID_DIRECTION_UNKNOWN = 0;

    public final static int SLID_DIRECTION_LEFT = 1;

    public final static int SLID_DIRECTION_RIGHT = 2;

    public final static int RELAY_ANIM_DURATION = 400;
    public final static int CANCEL_ANIM_DURATION = 200;

    private GradientDrawable topLeftGradientDrawable;
    private GradientDrawable topRightGradientDrawable;

    private GradientDrawable bottomLeftGradientDrawable;
    private GradientDrawable bottomRightGradientDrawable;

    private GradientDrawable topBGradientDrawable;
    private GradientDrawable bottomBGradientDrawable;

    private GradientDrawable topCGradientDrawable;
    private GradientDrawable bottomCGradientDrawable;

    private GradientDrawable coverGradientDrawable;

    public AnimHelper() {
        initGradient();
    }

    private void initGradient() {
        int deepColor = 0x33333333;
        int lightColor = 0x01333333;
        int[] gradientColors = new int[]{lightColor, deepColor};//渐变颜色数组
        topLeftGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        topLeftGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        bottomLeftGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        bottomLeftGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x22333333;
        lightColor = 0x01333333;
        gradientColors = new int[]{deepColor, lightColor, lightColor};
        topRightGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradientColors);
        topRightGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        bottomRightGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColors);
        bottomRightGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x55111111;
        lightColor = 0x00111111;
        gradientColors = new int[]{deepColor, lightColor};
        topBGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        topBGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        bottomBGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        bottomBGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);


        deepColor = 0x55333333;
        lightColor = 0x00333333;
        gradientColors = new int[]{lightColor, deepColor};
        topCGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        topCGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        bottomCGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        bottomCGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        deepColor = 0x55333333;
        lightColor = 0x00333333;
        gradientColors = new int[]{deepColor, lightColor};
        coverGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        coverGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }


    public GradientDrawable getTopLeftGradientDrawable() {
        return topLeftGradientDrawable;
    }

    public GradientDrawable getTopRightGradientDrawable() {
        return topRightGradientDrawable;
    }

    public GradientDrawable getBottomLeftGradientDrawable() {
        return bottomLeftGradientDrawable;
    }

    public GradientDrawable getBottomRightGradientDrawable() {
        return bottomRightGradientDrawable;
    }

    public GradientDrawable getTopBGradientDrawable() {
        return topBGradientDrawable;
    }

    public GradientDrawable getBottomBGradientDrawable() {
        return bottomBGradientDrawable;
    }

    public GradientDrawable getTopCGradientDrawable() {
        return topCGradientDrawable;
    }

    public GradientDrawable getBottomCGradientDrawable() {
        return bottomCGradientDrawable;
    }

    public GradientDrawable getCoverGradientDrawable() {
        return coverGradientDrawable;
    }
}
