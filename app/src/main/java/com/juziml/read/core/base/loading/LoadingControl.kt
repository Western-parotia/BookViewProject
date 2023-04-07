package com.juziml.read.core.base.loading

import androidx.lifecycle.MutableLiveData
import com.foundation.widget.utils.ext.smartPost

/**
 * create by zhusw on 7/28/21 13:59
 */
class LoadingControl(private val liveData: MutableLiveData<LoadingProgress>?) {

    fun doStart() {
        liveData?.smartPost(LoadingProgress(DataLoadingEvent.LOADING))
    }

    fun doSuccess() {
        liveData?.smartPost(LoadingProgress(DataLoadingEvent.SUCCESS))
    }

    fun doEmpty() {
        liveData?.smartPost(LoadingProgress(DataLoadingEvent.EMPTY))
    }

    fun doFail(code: String, msg: String) {
        liveData?.smartPost(LoadingProgress(DataLoadingEvent.FAILURE, code, msg))
    }

}