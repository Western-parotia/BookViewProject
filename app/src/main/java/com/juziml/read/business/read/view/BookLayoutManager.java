package com.juziml.read.business.read.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;


/**
 * create by zhusw on 2020-03-29 17:11
 */
public class BookLayoutManager extends RecyclerView.LayoutManager {
    private int bookFlipMode = BookFlipMode.MODE_CURL;

    private Context context;
    /**
     * 一次完整的聚焦滑动所需要的移动距离
     */
    private float onceCompleteScrollLength = -1;

    /**
     * 第一个子view的偏移量
     */
    private float firstChildCompleteScrollLength = -1;

    /**
     * 屏幕可见第一个view的position
     */
    private int firstVisiPos;

    /**
     * 屏幕可见的最后一个view的position
     */
    private int fastVisiPos;

    /**
     * 水平方向累计偏移量
     */
    private long horizontalOffset;

    private int childWidth = 0;
    private ValueAnimator selectAnimator;

    private boolean autoLeftScroll = false;

    private OnStopScroller onStopScroller;
    private OnForceLayoutCompleted onForceLayoutCompleted;

    public BookLayoutManager(Context context) {
        this.context = context;
    }

    public void setonStopScroller(OnStopScroller onStopScroller) {
        this.onStopScroller = onStopScroller;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setBookFlipMode(@BookFlipMode int bookFlipMode) {
        this.bookFlipMode = bookFlipMode;
        requestLayout();
    }

    public int getBookFlipMode() {
        return bookFlipMode;
    }

    public void setAutoLeftScroll(boolean autoLeftScroll) {
        this.autoLeftScroll = autoLeftScroll;
    }

    public void setOnForceLayoutCompleted(OnForceLayoutCompleted onForceLayoutCompleted) {
        this.onForceLayoutCompleted = onForceLayoutCompleted;
    }

    //确认允许水平滚动
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 返回用于布局的view数量，！= adapter 的 元素个数
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        // 分离全部已有的view 放入临时缓存  mAttachedScrap 集合中
        detachAndScrapAttachedViews(recycler);
        //确定布局，类似于 viewGroup的 onLayout
        layout(recycler, 0);
    }


    private int layout(RecyclerView.Recycler recycler, int dx) {
        int resultDelta = horizontalLayout(recycler, dx);
        recycleChildren(recycler);
        return resultDelta;
    }

    /**
     * 最大偏移量
     *
     * @return
     */
    private float getMaxOffset() {
        if (childWidth == 0 || getItemCount() == 0) return 0;
        return (childWidth) * (getItemCount() - 1);
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_DRAGGING://滑动手势开始
                cancelAnimator();
                break;

            case RecyclerView.SCROLL_STATE_IDLE://停止滑动
                final int selectPosition = findShouldSelectPosition();
                smoothScrollToPosition(selectPosition);
                if (null != onStopScroller) {
                    onStopScroller.onStop(autoLeftScroll, selectPosition);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 强制滚动到指定位置
     *
     * @param position
     */
    public void forceScrollToPosition(int position) {
        if (position > -1 && position < getItemCount()) {
            scrollToPosition(position, false);
        }
    }

    /**
     * 平滑滚动到某个位置
     *
     * @param position 目标Item索引
     */
    public void smoothScrollToPosition(int position) {
        if (position > -1 && position < getItemCount()) {
            scrollToPosition(position, true);
        }
    }

    public int findShouldSelectPosition() {
        if (onceCompleteScrollLength == -1 || firstVisiPos == -1) {
            return -1;
        }
        int position;
        if (autoLeftScroll) {
            position = (int) (Math.abs(horizontalOffset) / childWidth);
//            int remainder = (int) (Math.abs(horizontalOffset) % childWidth);
            // 超过临界距离 选中下一页，108 为 1/10的 1080 屏幕
            //固定临界值，避免屏幕越大需要滑动的距离越远
//            if (remainder >= ReadRecyclerView.MOVE_LEFT_MIN) {
            if (position + 1 <= getItemCount() - 1) {
                return position + 1;
            }
//            }
        } else {
            position = (int) (Math.abs(horizontalOffset) / childWidth);
        }

        return position;
    }

    private float getScrollToPositionOffset(int position) {
        return position * childWidth - Math.abs(horizontalOffset);
    }

    private void scrollToPosition(final int position, boolean withAnim) {
        cancelAnimator();
        final float distance = getScrollToPositionOffset(position);
        if (distance == 0) return;
        if (withAnim) {

            long minDuration = 100;
            long maxDuration = 300;
            long duration;

            float distanceFraction = (Math.abs(distance) / childWidth);

            if (distance <= childWidth) {
                duration = (long) (minDuration + (maxDuration - minDuration) * distanceFraction);
            } else {
                duration = (long) (maxDuration * distanceFraction);
            }
            selectAnimator = ValueAnimator.ofFloat(0.0f, distance);
            selectAnimator.setDuration(duration);
            selectAnimator.setInterpolator(new LinearInterpolator());
            final float startedOffset = horizontalOffset;
            selectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    horizontalOffset = (long) (startedOffset + value);
                    requestLayout();
                    if (value == distance) {//主动给一个滚动回调，因为不会处罚onScrollstop
                        if (null != onStopScroller) {
                            onStopScroller.onStop(distance > 0, position);
                        }
                    }
                }
            });
            selectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            selectAnimator.start();

        } else {
            horizontalOffset += (long) distance;
            requestLayout();
            if (null != onForceLayoutCompleted) {
                onForceLayoutCompleted.onLayoutCompleted(position);
            }
        }


    }

    public void cancelAnimator() {
        if (selectAnimator != null && (selectAnimator.isStarted() || selectAnimator.isRunning())) {
            selectAnimator.cancel();
        }
    }

    public boolean animIsRunning() {
        return selectAnimator != null && (selectAnimator.isStarted() || selectAnimator.isRunning());
    }

    private int horizontalLayout(RecyclerView.Recycler recycler, int dx) {
        //-----------------------1 边界检测---------------
        //已达左边界
        if (dx < 0) {
            if (horizontalOffset < 0) {
                dx = 0;
                horizontalOffset = dx;
            }
        }
        //确认右边界
        if (dx > 0) {
            if (horizontalOffset >= getMaxOffset()) {
                horizontalOffset = (long) getMaxOffset();
                dx = 0;
            }
        }
        // 分离全部的view，加入到临时缓存，这里调用，因为在滑动过程中，可显view 可能发生改变
        detachAndScrapAttachedViews(recycler);

        //-----------------------2 计算用于 view 确定位置的参数---------------

        float layoutX = 0;
        float fraction = 0;

        View tempView = null;

        int tempPosition = -1;

        if (onceCompleteScrollLength == -1) {
            // 因为firstVisiPos在下面可能被改变，所以用tempPosition暂存一下
            tempPosition = firstVisiPos;
            tempView = recycler.getViewForPosition(tempPosition);
            measureChildWithMargins(tempView, 0, 0);
            //以第一个子view宽度为计算标准，这样就不支持 itemType 了。全部item要保持宽度一样，margin参数一样
            childWidth = getDecoratedMeasurementHorizontal(tempView);
        }
        // 修正第一个可见view firstVisiPos 已经滑动了多少个完整的onceCompleteScrollLength就代表滑动了多少个item

        firstChildCompleteScrollLength = getWidth() / 2 + childWidth / 2;

        if (horizontalOffset >= firstChildCompleteScrollLength) {
            layoutX = 0;
            onceCompleteScrollLength = childWidth;
            //计算 滚动到了 哪个view的区域
            firstVisiPos = (int) Math.floor(Math.abs(horizontalOffset - firstChildCompleteScrollLength) / onceCompleteScrollLength) + 1;
            fraction = (Math.abs(horizontalOffset - firstChildCompleteScrollLength) % onceCompleteScrollLength) / (onceCompleteScrollLength * 1.0f);
        } else {
            firstVisiPos = 0;
            layoutX = getMinOffset();
            //记录单个view 需要滚动的距离
            onceCompleteScrollLength = firstChildCompleteScrollLength;
            fraction = (Math.abs(horizontalOffset) % onceCompleteScrollLength) / (onceCompleteScrollLength * 1.0f);
        }
        // 临时将fastVisiPos赋值为getItemCount() - 1，放心，下面遍历时会判断view是否已溢出屏幕，并及时修正该值并结束布局
        //注意这里 是adapter 的 个数，而不是 state 保存的view 显示数量
        fastVisiPos = getItemCount() - 1;
        //这里似乎多此一举了 ，可以把 分母直接替换为 normalViewOffset 在上面的逻辑中计算出来
        float normalViewOffset = onceCompleteScrollLength * fraction;
        boolean isNormalViewOffsetSetted = false;

        //-----------------------3 确认view位置---------------
        for (int itemIndex = firstVisiPos; itemIndex <= fastVisiPos; itemIndex++) {

            View itemView;

            // 如果初始化数据时已经取了一个临时view
            if (itemIndex == tempPosition && null != tempView) {
                itemView = tempView;
            } else {
                itemView = recycler.getViewForPosition(itemIndex);
            }
            //计算新view 位置
            int focusPosition = (int) (Math.abs(horizontalOffset) / (childWidth));

            if (itemIndex <= focusPosition) {
                addView(itemView);
            } else {
                addView(itemView, 0);
            }
            //测量新view
            measureChildWithMargins(itemView, 0, 0);
            if (!isNormalViewOffsetSetted) {
                layoutX -= normalViewOffset;
                isNormalViewOffsetSetted = true;
            }
            //计算view layout 坐标
            int left, top, right, bottom;
            left = (int) layoutX;
            top = 0;
            right = left + getDecoratedMeasurementHorizontal(itemView);
            bottom = top + getDecoratedMeasurementVertical(itemView);
            layoutDecoratedWithMargins(itemView, left, top, right, bottom);
            //更新下一个view X 轴坐标
            layoutX += childWidth;
            //修正溢出屏幕的view个数
            if (layoutX > getWidth() - getPaddingRight()) {
                fastVisiPos = itemIndex;
                break;
            }
        }//end for
        return dx;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (dx == 0 || getChildCount() == 0) {

            return 0;
        }
        horizontalOffset += dx;
        dx = layout(recycler, dx);

        return dx;
    }

    /**
     * 回收需回收的item
     */
    private void recycleChildren(RecyclerView.Recycler recycler) {
        List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        for (int i = 0; i < scrapList.size(); i++) {
            RecyclerView.ViewHolder holder = scrapList.get(i);
            removeAndRecycleView(holder.itemView, recycler);
        }
    }

    /**
     * 获取某个childView在竖直方向所占的空间,将margin考虑进去
     * 包含了指示器
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    /**
     * 获取某个childView在水平方向所占的空间，将margin考虑进去
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取最小的偏移量
     *
     * @return
     */
    private float getMinOffset() {
        if (childWidth == 0) return 0;
        return (getWidth() - childWidth) / 2;
    }

    protected void onRecyclerViewSizeChange() {
        onceCompleteScrollLength = -1;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef({
            BookFlipMode.MODE_NORMAL,
            BookFlipMode.MODE_COVER,
            BookFlipMode.MODE_CURL,

    })
    public @interface BookFlipMode {
        int MODE_NORMAL = 1;
        int MODE_COVER = 2;
        int MODE_CURL = 3;

    }

    public interface OnStopScroller {
        void onStop(boolean autoLeftScroll, int curPos);
    }

    public interface OnForceLayoutCompleted {
        void onLayoutCompleted(final int curPos);
    }
}
