package com.juziml.read.utils.ext

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

/**
 * 设置text并把光标移到最后
 */
fun EditText.setTextAndSelection(text: CharSequence?) {
    setText(text)
    setSelection(getText()?.length ?: 0)
}

/**
 * 用户点击键盘搜索的监听
 *
 * 请自行在xml加上：android:imeOptions="actionSearch"
 */
fun EditText.setOnSearchClickListener(listener: () -> Unit) {
    //标准写法
    setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
        //获得键盘中搜索的事件
        if (actionId == KeyEvent.KEYCODE_SEARCH || actionId == EditorInfo.IME_ACTION_SEARCH) {
            listener()
            return@OnEditorActionListener true
        }
        false
    })
    //部分输入法可能不支持，保底策略
    setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            listener()
            return@OnKeyListener true
        }
        false
    })
}