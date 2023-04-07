package com.juziml.read.utils.ext

import android.widget.RadioButton
import com.foundation.widget.rg.MjRadioGroup

private const val CLICK_INTERVAL = 300L

/**
 * 避免快速点击
 */
fun MjRadioGroup.setOnItemShakeLessClickListener(
    clickInterval: Long = CLICK_INTERVAL,
    block: ((rb: RadioButton, position: Int) -> Unit)
) {
    var timestamp = System.currentTimeMillis()
    var oldPosition = checkedPosition
    setOnItemClickListener { rb, position ->
        val interval = System.currentTimeMillis() - timestamp
        if (interval >= clickInterval) {
            oldPosition = position
            block(rb, position)
        } else {
            if (oldPosition != position) {
                setCheckedPosition(oldPosition)
            }
        }
        timestamp = System.currentTimeMillis()
    }

}