package com.juziml.read.utils.ext

import android.widget.Toast
import com.juziml.read.core.App

/**
 * create by zhusw on 6/9/21 14:13
 */

fun CharSequence?.toast() {
    Toast.makeText(App.get(), this, Toast.LENGTH_SHORT).show()
}
