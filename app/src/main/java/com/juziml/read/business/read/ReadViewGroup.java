package com.juziml.read.business.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by zhusw on 2020-08-14 17:33
 */
public class ReadViewGroup extends FrameLayout implements CurlAnimParentView {

    private ReadRecyclerViewV2 readRecyclerViewV2;
    private ReadAnimView readAnimView;
    private AnimHelper animHelper;

    private OnClickMenuListener onClickMenuListener;

    private int itemViewBackgroundColor = Color.WHITE;
    private Runnable dataPendIntentTask;

    public ReadViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public ReadViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animHelper = new AnimHelper();
        init();
    }

    private void init() {
        removeAllViews();
        readRecyclerViewV2 = new ReadRecyclerViewV2(getContext());
        readAnimView = new ReadAnimView(getContext());
        readAnimView.setAnimMode(readRecyclerViewV2.getFlipMode());
        readRecyclerViewV2.bindReadCurlAnimProxy(readAnimView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(readRecyclerViewV2, params);

        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params2.gravity = Gravity.CENTER;
        addView(readAnimView, params2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        readRecyclerViewV2.bindReadCurlAnimProxy(readAnimView);

    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        readRecyclerViewV2.setOnPositionChangedListener(onPositionChangedListener);

    }

    public void setOnClickMenuListener(OnClickMenuListener onClickMenuListener) {
        this.onClickMenuListener = onClickMenuListener;
    }

    public void scrollToPosition(int position) {
        readRecyclerViewV2.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        readRecyclerViewV2.smoothScrollToPosition(position);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        readRecyclerViewV2.setAdapter(adapter);
    }

    public void setItemViewBackgroundColor(int itemViewBackgroundColor) {
        this.itemViewBackgroundColor = itemViewBackgroundColor;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != dataPendIntentTask) {
            readRecyclerViewV2.removeCallbacks(dataPendIntentTask);
        }
    }

    @Override
    public void onExpectNext() {
        readRecyclerViewV2.onExpectNext(false);
        if (null != dataPendIntentTask) {
            readRecyclerViewV2.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public void onExpectPrevious() {
        readRecyclerViewV2.onExpectPrevious(false);
        if (null != dataPendIntentTask) {
            readRecyclerViewV2.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return readRecyclerViewV2.getPreviousBitmap();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return readRecyclerViewV2.getCurrentBitmap();
    }

    @Override
    public Bitmap getNextBitmap() {
        return readRecyclerViewV2.getNextBitmap();
    }

    @Override
    public int getBackgroundColor() {
        return itemViewBackgroundColor;
    }


    public void setFlipMode(int flipMode) {
        if (flipMode == ReadLayoutManagerV2.BookFlipMode.MODE_CURL
                || flipMode == ReadLayoutManagerV2.BookFlipMode.MODE_COVER) {
            readAnimView.setVisibility(View.VISIBLE);
        } else {
            readAnimView.setVisibility(View.INVISIBLE);//不可以设置为gone，避免animView 无法获取尺寸
        }
        readRecyclerViewV2.setFlipMode(flipMode);
        readAnimView.setAnimMode(flipMode);

    }

    @Override
    public AnimHelper getAnimHelper() {
        return animHelper;
    }

    @Override
    public void onClickMenuArea() {
        if (null != onClickMenuListener) {
            onClickMenuListener.onClickMenu();
        }
    }

    /**
     * 只在非卷曲模式下调用
     */
    @Override
    public void onClickNextArea() {
        readRecyclerViewV2.onExpectNext(true);
    }

    /**
     * 只在非卷曲模式下调用
     */
    @Override
    public void onClickPreviousArea() {
        readRecyclerViewV2.onExpectPrevious(true);
    }

    public boolean checkAllowChangeData() {
        return !readAnimView.animRunningOrTouching()
                || readRecyclerViewV2.getFlipMode() == ReadLayoutManagerV2.BookFlipMode.MODE_NORMAL;
    }

    /**
     * @param dataPendIntentTask
     */
    public void setDataPendIntentTask(Runnable dataPendIntentTask) {
        this.dataPendIntentTask = dataPendIntentTask;
    }

    public interface OnPositionChangedListener {
        void onChanged(boolean arriveNext, int curPosition);
    }

    public interface OnClickMenuListener {
        void onClickMenu();
    }


}
