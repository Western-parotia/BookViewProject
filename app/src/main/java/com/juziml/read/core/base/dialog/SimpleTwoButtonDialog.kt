package com.juziml.read.core.base.dialog

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.juziml.read.R
import com.juziml.read.databinding.DialogSimpleTwoButtonBinding
import com.juziml.read.utils.ext.dp
import com.juziml.read.utils.ext.toColorInt

/**
 * 标题、内容、确定、取消弹窗
 * 如果字符串为空则隐藏
 *
 * create by zhusw on 10/7/21 10:16
 * 如果text为空则隐藏
 */
class SimpleTwoButtonDialog @JvmOverloads constructor(
    activity: ComponentActivity,
    var title: String = "提示",
    var content: CharSequence = "",
    var cancel: String = "取消",
    var confirm: String = "确认",
    var onConfirm: (() -> Unit)? = null,
    var onCancel: (() -> Unit)? = null
) :
    ArchDialog<DialogSimpleTwoButtonBinding>(activity) {

    /**
     * true：拦截并处理
     * false：默认关闭
     */
    var onBackInterceptorListener: ((dialog: SimpleTwoButtonDialog) -> Boolean)? = null

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
            msg: String,
            onConfirm: (() -> Unit)? = null
        ): SimpleTwoButtonDialog {
            return SimpleTwoButtonDialog(
                activity = activity,
                content = msg,
                onConfirm = onConfirm
            )
        }
    }

    override fun convertView(binding: DialogSimpleTwoButtonBinding) {
        binding.tvConfirm.setOnClickListener {
            dismiss()
            onConfirm?.invoke()
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
        binding.tvContent.text = content
        binding.tvContent.isVisible = content.isNotEmpty()
        if (content is SpannableStringBuilder)
            binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        binding.tvCancel.text = cancel
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