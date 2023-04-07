package com.juziml.read.core.base.loading

import android.view.View
import androidx.annotation.Size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.foundation.widget.loading.IPageLoading
import com.foundation.widget.loading.PageLoadingAdapter
import com.foundation.widget.utils.ext.smartPost
import com.foundation.widget.utils.other.MjPage
import com.juziml.read.utils.ext.allTrue
import com.juziml.read.utils.ext.oneTrue
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.finishRefreshLoadMore
import com.scwang.smart.refresh.layout.isEnabledLoadMore
import com.scwang.smart.refresh.layout.isEnabledRefresh

/**
 * create by zhusw on 8/17/21 10:09
 */
object LoadingEventHelper {

    fun bindLoadingEvent(
        owner: LifecycleOwner,
        liveData: LiveData<LoadingProgress>,
        loadingView: IPageLoading,
        adapter: PageLoadingAdapter,
        onClickRetry: (() -> Unit)? = null
    ) {
        loadingView.failViewEventListener = { _: View, _: Int, _: Any? ->
            onClickRetry?.invoke()
        }
        loadingView.setLoadingAdapter(adapter)
        val observer = Observer<LoadingProgress> {
            when (it.event) {
                DataLoadingEvent.LOADING -> {
                    loadingView.showLoading(true)
                }
                DataLoadingEvent.SUCCESS -> {
                    loadingView.stop()
                }
                DataLoadingEvent.FAILURE -> {
                    loadingView.showLoadingFail(true)
                }
                DataLoadingEvent.EMPTY -> {
                    loadingView.showEmptyView()
                }
            }
        }
        liveData.observe(owner, observer)
    }

    /**
     * 多liveData绑定loading（一般为全局loading）
     *
     * loading：有一个开始loading就发送loading事件
     * success：全部为success或empty才发送success事件
     * feature：有一个有feature就发送feature事件
     *
     * 注意：init时所有的LiveData都应该触发，不能只有某一个处于init状态（尤其是list列表）
     *      示例类：OftenBuyListFragment.bindData()-bind...
     * 注意：handler拦截错误码返回true时不会再收到事件，请主动发送
     *      示例类：OftenBuyListVM.loadOftenBuyList()-loadingControl.doSuccess()
     * 注意：不可重复调用
     *
     * @param loadingView 单独的view，不要共用其他loading
     */
    fun bindMultiLoadingEvent(
        owner: LifecycleOwner,
        vararg liveDatas: LiveData<LoadingProgress>,
        loadingView: IPageLoading,
        adapter: PageLoadingAdapter,
        onClickRetry: (() -> Unit)? = null
    ) {
        bindLoadingEvent(
            owner,
            mergeMultiLiveData(owner, *liveDatas),
            loadingView,
            adapter,
            onClickRetry
        )
    }

    /**
     * 合并多个LiveData事件
     * loading：有一个开始loading就发送loading事件
     * success：全部为success或empty才发送success事件
     * feature：有一个有feature就发送feature事件
     * @return 返回合并后的新LiveData
     */
    fun mergeMultiLiveData(
        owner: LifecycleOwner,
        @Size(min = 1) vararg liveDatas: LiveData<LoadingProgress>
    ): LiveData<LoadingProgress> {
        if (liveDatas.isEmpty()) {
            throw IllegalArgumentException("至少要有一个liveData")
        }
        val ld = MutableLiveData<LoadingProgress>()

        liveDatas.forEach { liveData ->
            val observer = Observer<LoadingProgress> { progress ->
                when (progress.event) {
                    DataLoadingEvent.LOADING -> {
                        //其他的有没有loading中
                        val hasOtherLoading = liveDatas.oneTrue {
                            it.value != progress && it.value?.event == DataLoadingEvent.LOADING
                        }
                        if (!hasOtherLoading) {
                            //只有自己loading（第一个loading）
                            ld.smartPost(progress)
                        }
                    }
                    DataLoadingEvent.SUCCESS, DataLoadingEvent.EMPTY -> {
                        val allOk = liveDatas.allTrue {
                            val event = it.value?.event
                            event == DataLoadingEvent.SUCCESS || event == DataLoadingEvent.EMPTY
                        }
                        if (allOk) {
                            //全部都成功才会发送成功事件
                            ld.smartPost(LoadingProgress(DataLoadingEvent.SUCCESS))
                        }
                    }
                    DataLoadingEvent.FAILURE -> {
                        //其他的有没有失败
                        val hasOtherFailure = liveDatas.oneTrue {
                            it.value != progress && it.value?.event == DataLoadingEvent.FAILURE
                        }
                        if (!hasOtherFailure) {
                            //只有自己失败（第一个失败）
                            ld.smartPost(progress)
                        }
                    }
                }
            }
            liveData.observe(owner, observer)
        }
        return ld
    }

    fun bindRefreshLoadingEvent(
        owner: LifecycleOwner,
        liveData: LiveData<LoadingProgress>,
        refreshLayout: SmartRefreshLayout,
    ) {
        liveData.observe(owner) {
            when (it.event) {
                DataLoadingEvent.LOADING -> {

                }
                DataLoadingEvent.EMPTY -> {
                    refreshLayout.finishRefresh()
                }
                DataLoadingEvent.FAILURE -> {
                    refreshLayout.finishRefresh()
                }
                DataLoadingEvent.SUCCESS -> {
                    refreshLayout.finishRefresh()
                }
            }
        }
    }

    /**
     * refresh时将处理 EMPTY FAILURE 两种状态 PageLoading
     *
     * 会关掉上拉和下拉，有数据时再打开
     */
    fun bindInitAndRefreshLoadingEvent(
        owner: LifecycleOwner,
        initLiveData: LiveData<LoadingProgress>,
        refreshLiveData: LiveData<LoadingProgress>,
        loadingView: IPageLoading,
        refreshLayout: SmartRefreshLayout,
        adapter: PageLoadingAdapter,
        onClickRetry: (() -> Unit)? = null
    ) {
        bindLoadingEvent(
            owner = owner,
            liveData = initLiveData,
            adapter = adapter,
            loadingView = loadingView,
            onClickRetry = onClickRetry
        )

        val oldEnabledRefresh = refreshLayout.isEnabledRefresh
        val oldEnabledLoadMore = refreshLayout.isEnabledLoadMore
        initLiveData.observe(owner) {
            when (it.event) {
                DataLoadingEvent.LOADING -> {
                    //init时将上拉下拉禁掉，防止再次被拉出来（用于smart里套loading的效果）
                    refreshLayout.isEnabledRefresh = false
                    refreshLayout.isEnabledLoadMore = false
                    if (oldEnabledLoadMore) {
                        refreshLayout.resetNoMoreData()
                    }
                }
                DataLoadingEvent.EMPTY -> {
                    refreshLayout.isEnabledRefresh = oldEnabledRefresh
                    refreshLayout.finishRefreshLoadMore()
                }
                DataLoadingEvent.FAILURE -> {
                    refreshLayout.isEnabledRefresh = oldEnabledRefresh
                    refreshLayout.finishRefreshLoadMore()
                }
                DataLoadingEvent.SUCCESS -> {
                    refreshLayout.finishRefreshLoadMore()
                    refreshLayout.isEnabledRefresh = oldEnabledRefresh
                    refreshLayout.isEnabledLoadMore = oldEnabledLoadMore
                }
            }
        }

        refreshLiveData.observe(owner) {
            when (it.event) {
                DataLoadingEvent.LOADING -> {
                    if (oldEnabledLoadMore) {
                        refreshLayout.resetNoMoreData()
                    }
                }
                DataLoadingEvent.EMPTY -> {
                    refreshLayout.finishRefreshLoadMore()
                    loadingView.showEmptyView()
                }
                DataLoadingEvent.FAILURE -> {
                    refreshLayout.finishRefreshLoadMore()
                    loadingView.showLoadingFail(true)
                }
                DataLoadingEvent.SUCCESS -> {
                    refreshLayout.finishRefreshLoadMore()
                    loadingView.stop()
                }
            }
        }
    }


    fun bindLoadMoreLoadingEvent(
        owner: LifecycleOwner,
        liveData: LiveData<LoadingProgress>,
        refreshLayout: SmartRefreshLayout
    ) {
        liveData.observe(owner) {
            when (it.event) {
                DataLoadingEvent.LOADING -> {

                }
                DataLoadingEvent.EMPTY -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
                DataLoadingEvent.FAILURE -> {
                    refreshLayout.finishLoadMore(false)
                }
                DataLoadingEvent.SUCCESS -> {
                    refreshLayout.finishLoadMore()
                }
            }
        }
    }

    /**
     * 绑定分页逻辑，可以回退和重新init
     */
    fun bindPageEvent(
        owner: LifecycleOwner,
        initLiveData: LiveData<LoadingProgress>,
        loadMoreLiveData: LiveData<LoadingProgress>,
        page: MjPage
    ) {
        initLiveData.observe(owner) {
            if (it.event == DataLoadingEvent.EMPTY || it.event == DataLoadingEvent.FAILURE) {
                page.reInit()
            }
        }
        loadMoreLiveData.observe(owner) {
            if (it.event == DataLoadingEvent.FAILURE) {
                page.previousPage()
            }
        }
    }
}