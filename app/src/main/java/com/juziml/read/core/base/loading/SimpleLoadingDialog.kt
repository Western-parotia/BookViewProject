package com.juziml.read.core.base.loading

import android.view.MotionEvent
import androidx.lifecycle.LiveData
import com.foundation.widget.utils.ext.view.requireActivity
import com.foundation.widget.utils.ui.IUIContext
import com.juziml.read.core.base.dialog.ArchDialog
import com.juziml.read.databinding.AppLayoutLoadProgressStyleNetBinding

/**
 * 轻量loading弹窗，支持返回和事件透传
 */
class SimpleLoadingDialog(private val ui: IUIContext) :
    ArchDialog<AppLayoutLoadProgressStyleNetBinding>(ui.requireActivity) {

    override fun convertView(binding: AppLayoutLoadProgressStyleNetBinding) {
    }

    override fun getDimAmount() = 0f

    override fun getAnimStyle() = 0

    /**
     * 后期将增加左上角支持返回功能
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 如果想绑定多个，请先合并多个liveData：[LoadingEventHelper.mergeMultiLiveData]
     */
    fun bindLoadingEvent(ld: LiveData<LoadingProgress>) {
        ld.observe(ui) {
            when (it.event) {
                DataLoadingEvent.LOADING -> show()
                DataLoadingEvent.SUCCESS -> dismiss()
                DataLoadingEvent.FAILURE -> dismiss()
                DataLoadingEvent.EMPTY -> dismiss()
            }
        }
    }
}