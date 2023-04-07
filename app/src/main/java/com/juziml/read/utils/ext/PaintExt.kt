package com.juziml.read.utils.ext

import android.graphics.Paint
import com.juziml.read.core.ArchConfig

/**
 * TextView侧边加小icon的辅助方法
 * 返回TextView加的空格数和单行高度，方便代码设置
 *
 * @param spacePx 需要加空格的大小，如：icon.with
 * @return 空格数和单行文本高度
 */
fun Paint.getSpannableSpace(spacePx: Int): SpannableSpaceBean {
    val oneSpaceWidth = measureText("          ") / 10f
    var spaceCount = (spacePx / oneSpaceWidth + 0.5f).toInt()
    if (spaceCount > 10000) {
        spaceCount = 10000
        if (ArchConfig.debug) {
            throw IllegalArgumentException("传入的宽太大：$spacePx，oneSpaceWidth：$oneSpaceWidth")
        }
    }
    val builder = StringBuilder()
    for (i in 0 until spaceCount) {
        builder.append(" ")
    }
    val fm = fontMetrics
    return SpannableSpaceBean(builder.toString(), (fm.bottom - fm.top + 0.5f).toInt())
}

/**
 * @param spaces 就是一堆空格
 * @param textHeight 文字高度，便于自行居中展示
 */
data class SpannableSpaceBean(
    val spaces: String,
    val textHeight: Int
)