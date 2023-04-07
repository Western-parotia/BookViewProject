package com.juziml.read.business.read.view;

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

import com.juziml.read.business.read.anim.AnimHelper;

/**
 * create by zhusw on 2020-08-14 17:33
 */
public class ReadViewGroup extends FrameLayout implements CurlAnimParentView {

    private ReadRecyclerView readRecyclerView;
    private ReadAnimView readAnimView;
    private final AnimHelper animHelper;

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
        readRecyclerView = new ReadRecyclerView(getContext());
        readAnimView = new ReadAnimView(getContext());
        readAnimView.setAnimMode(readRecyclerView.getFlipMode());
        readRecyclerView.bindReadCurlAnimProxy(readAnimView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(readRecyclerView, params);

        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params2.gravity = Gravity.CENTER;
        addView(readAnimView, params2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        readRecyclerView.bindReadCurlAnimProxy(readAnimView);

    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        readRecyclerView.setOnPositionChangedListener(onPositionChangedListener);

    }

    public void setOnClickMenuListener(OnClickMenuListener onClickMenuListener) {
        this.onClickMenuListener = onClickMenuListener;
    }

    public void scrollToPosition(int position) {
        readRecyclerView.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        readRecyclerView.smoothScrollToPosition(position);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        readRecyclerView.setAdapter(adapter);
    }

    public void setItemViewBackgroundColor(int itemViewBackgroundColor) {
        this.itemViewBackgroundColor = itemViewBackgroundColor;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != dataPendIntentTask) {
            readRecyclerView.removeCallbacks(dataPendIntentTask);
        }
    }

    @Override
    public void onExpectNext() {
        readRecyclerView.onExpectNext(false);
        if (null != dataPendIntentTask) {
            readRecyclerView.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public void onExpectPrevious() {
        readRecyclerView.onExpectPrevious(false);
        if (null != dataPendIntentTask) {
            readRecyclerView.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return readRecyclerView.getPreviousBitmap();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return readRecyclerView.getCurrentBitmap();
    }

    @Override
    public Bitmap getNextBitmap() {
        return readRecyclerView.getNextBitmap();
    }

    @Override
    public int getBackgroundColor() {
        return itemViewBackgroundColor;
    }


    public void setFlipMode(int flipMode) {
        if (flipMode == ReadLayoutManager.BookFlipMode.MODE_CURL
                || flipMode == ReadLayoutManager.BookFlipMode.MODE_COVER) {
            readAnimView.setVisibility(View.VISIBLE);
        } else {
            readAnimView.setVisibility(View.INVISIBLE);//不可以设置为gone，避免animView 无法获取尺寸
        }
        readRecyclerView.setFlipMode(flipMode);
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
        readRecyclerView.onExpectNext(true);
    }

    /**
     * 只在非卷曲模式下调用
     */
    @Override
    public void onClickPreviousArea() {
        readRecyclerView.onExpectPrevious(true);
    }

    public boolean checkAllowChangeData() {
        return !readAnimView.animRunningOrTouching()
                || readRecyclerView.getFlipMode() == ReadLayoutManager.BookFlipMode.MODE_NORMAL;
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
