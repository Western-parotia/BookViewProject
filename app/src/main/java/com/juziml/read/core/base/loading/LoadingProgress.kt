package com.juziml.read.core.base.loading

/**
 * create by zhusw on 6/23/21 14:51
 */
data class LoadingProgress(
    val event: DataLoadingEvent,
    val code: String = "",
    val msg: String = ""
)