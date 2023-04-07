package com.juziml.read.core.base.dialog

import androidx.activity.ComponentActivity
import androidx.viewbinding.ViewBinding
import com.foundation.app.basepopupwindow.BaseViewBindingPopupWindow

/**
 * @作者 王能
 * @时间 2022/2/9 14:40
 */
abstract class ArchPopupWindow<T : ViewBinding>(
    activity: ComponentActivity,
) : BaseViewBindingPopupWindow<T>(activity) {
    override fun onDestroyDialog() {
    }

    override fun onDismiss() {
    }
}