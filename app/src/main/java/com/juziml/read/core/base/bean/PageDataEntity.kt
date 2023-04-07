package com.juziml.read.core.base.bean

/**
 * 基类page
 */
data class PageDataEntity<T>(
    val page_count: Int = 0,
    val total_record: Int = 0,
    val page_current: Int = 0,
    val page_size: Int = 0,
    val list: List<T>? = null,
)
