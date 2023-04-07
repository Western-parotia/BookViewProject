package com.juziml.read.core.base.loading

import android.view.LayoutInflater
import android.view.View
import com.foundation.widget.loading.PageLoadingAdapter
import com.juziml.read.R

/**
 * 购物端默认加载动画适配器
 * create by zhusw on 7/2/21 17:58
 */
open class NormalLoadingAdapter : PageLoadingAdapter() {

    override fun getLoadingFailView(): View {
        return LayoutInflater.from(attachContext)
            .inflate(R.layout.app_layout_load_error_style_net, parentView, false)
    }

    override fun getEmptyView(): View {
        return LayoutInflater.from(attachContext)
            .inflate(R.layout.app_layout_load_empty_style_net, parentView, false)
    }

    override fun getLoadingView(): View {
        return LayoutInflater.from(attachContext)
            .inflate(R.layout.app_layout_load_progress_style_net, parentView, false)
    }
}