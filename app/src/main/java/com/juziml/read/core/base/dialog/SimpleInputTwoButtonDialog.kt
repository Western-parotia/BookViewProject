package com.juziml.read.core.base.dialog

import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.juziml.read.R
import com.juziml.read.databinding.DialogSimpleTwoButtonInputBinding
import com.juziml.read.utils.ext.dp
import com.juziml.read.utils.ext.toColorInt
import com.juziml.read.utils.ext.toast

/**
 * 标题、内容、确定、取消弹窗
 * 如果字符串为空则隐藏
 *
 * create by zhusw on 10/7/21 10:16
 * 如果text为空则隐藏
 */
class SimpleInputTwoButtonDialog @JvmOverloads constructor(
    activity: ComponentActivity,
    var title: String = "提示",
    var hint: String = "",
    var cancel: String = "取消",
    var confirm: String = "确认",
    var onConfirm: ((text: String) -> Unit)? = null,
    var onCancel: (() -> Unit)? = null
) :
    ArchDialog<DialogSimpleTwoButtonInputBinding>(activity) {

    /**
     * true：拦截并处理
     * false：默认关闭
     */
    var onBackInterceptorListener: ((dialog: SimpleInputTwoButtonDialog) -> Boolean)? = null

    /**
     * 标题颜色，如：红色、黑色
     */
    @ColorInt
    var titleColor = R.color.color_333333.toColorInt

    /**
     * 确认颜色，如：红色、黑色
     */
    @ColorInt
    var confirmColor = R.color.color_FFFFFF.toColorInt

    /**
     * 确认圆角色，如：红色、黑色
     */
    @ColorInt
    var confirmBackgroundColor = R.color.colorPrimary.toColorInt

    /**
     * 确定取消按钮的圆角大小，如：两边圆形效果500.dp/1000
     */
    var buttonRadius = 500.dp

    companion object {
        fun createWithConfirm(
            activity: ComponentActivity,
            hint: String = "请输入/粘贴内容",
            onConfirm: ((text: String) -> Unit)? = null
        ): SimpleInputTwoButtonDialog {
            return SimpleInputTwoButtonDialog(
                activity = activity,
                hint = hint,
                onConfirm = onConfirm
            )
        }
    }

    override fun convertView(binding: DialogSimpleTwoButtonInputBinding) {
        binding.tvConfirm.setOnClickListener {
            val text = binding.etContent.text.toString()
            if (text.isNotEmpty()) {
                dismiss()
                onConfirm?.invoke(text)
            } else {
                "请输入内容后再确认".toast()
            }
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
            onCancel?.invoke()
        }

    }

    override fun onShow() {
        super.onShow()
        binding.tvTitle.text = title
        binding.tvTitle.setTextColor(titleColor)
        binding.tvTitle.isVisible = title.isNotEmpty()
        binding.tvCancel.text = cancel
        binding.etContent.hint = hint
        binding.tvCancel.buildShape().setCornersRadius(buttonRadius)
        binding.tvCancel.isVisible = cancel.isNotEmpty()
        binding.tvConfirm.text = confirm
        binding.tvConfirm.setTextColor(confirmColor)
        binding.tvConfirm.buildShape().setCornersRadius(buttonRadius)
        binding.tvConfirm.buildShape().setSolidColorInt(confirmBackgroundColor)
        binding.tvConfirm.isVisible = confirm.isNotEmpty()
    }

    override fun onBackPressed() {
        //没有拦截就走super关闭
        if (onBackInterceptorListener?.invoke(this) != true) {
            super.onBackPressed()
        }
    }
}