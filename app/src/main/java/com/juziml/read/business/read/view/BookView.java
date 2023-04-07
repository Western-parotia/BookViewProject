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
public class BookView extends FrameLayout implements AnimParentView {

    private BookRecyclerView bookRecyclerView;
    private PuppetView puppetView;
    private final AnimHelper animHelper;

    private OnClickMenuListener onClickMenuListener;

    private int itemViewBackgroundColor = Color.WHITE;
    private Runnable dataPendIntentTask;

    public BookView(@NonNull Context context) {
        this(context, null);
    }

    public BookView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animHelper = new AnimHelper();
        init();
    }

    private void init() {
        removeAllViews();
        bookRecyclerView = new BookRecyclerView(getContext());
        puppetView = new PuppetView(getContext());
        puppetView.setAnimMode(bookRecyclerView.getFlipMode());
        bookRecyclerView.bindReadCurlAnimProxy(puppetView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(bookRecyclerView, params);

        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params2.gravity = Gravity.CENTER;
        addView(puppetView, params2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bookRecyclerView.bindReadCurlAnimProxy(puppetView);

    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        bookRecyclerView.setOnPositionChangedListener(onPositionChangedListener);

    }

    public void setOnClickMenuListener(OnClickMenuListener onClickMenuListener) {
        this.onClickMenuListener = onClickMenuListener;
    }

    public void scrollToPosition(int position) {
        bookRecyclerView.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        bookRecyclerView.smoothScrollToPosition(position);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        bookRecyclerView.setAdapter(adapter);
    }

    public void setItemViewBackgroundColor(int itemViewBackgroundColor) {
        this.itemViewBackgroundColor = itemViewBackgroundColor;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != dataPendIntentTask) {
            bookRecyclerView.removeCallbacks(dataPendIntentTask);
        }
    }

    @Override
    public void onExpectNext() {
        bookRecyclerView.onExpectNext(false);
        if (null != dataPendIntentTask) {
            bookRecyclerView.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public void onExpectPrevious() {
        bookRecyclerView.onExpectPrevious(false);
        if (null != dataPendIntentTask) {
            bookRecyclerView.post(dataPendIntentTask);
            dataPendIntentTask = null;
        }
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return bookRecyclerView.getPreviousBitmap();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return bookRecyclerView.getCurrentBitmap();
    }

    @Override
    public Bitmap getNextBitmap() {
        return bookRecyclerView.getNextBitmap();
    }

    @Override
    public int getBackgroundColor() {
        return itemViewBackgroundColor;
    }


    public void setFlipMode(int flipMode) {
        if (flipMode == BookLayoutManager.BookFlipMode.MODE_CURL
                || flipMode == BookLayoutManager.BookFlipMode.MODE_COVER) {
            puppetView.setVisibility(View.VISIBLE);
        } else {
            puppetView.setVisibility(View.INVISIBLE);//不可以设置为gone，避免animView 无法获取尺寸
        }
        bookRecyclerView.setFlipMode(flipMode);
        puppetView.setAnimMode(flipMode);

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
        bookRecyclerView.onExpectNext(true);
    }

    /**
     * 只在非卷曲模式下调用
     */
    @Override
    public void onClickPreviousArea() {
        bookRecyclerView.onExpectPrevious(true);
    }

    public boolean checkAllowChangeData() {
        return !puppetView.animRunningOrTouching()
                || bookRecyclerView.getFlipMode() == BookLayoutManager.BookFlipMode.MODE_NORMAL;
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
