package com.juziml.read.core.base.dialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.StyleRes
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import com.foundation.app.arc.utils.ext.AFViewModelLazy
import com.foundation.app.arc.utils.ext.lazyAtomic
import com.foundation.app.basedialog.BaseViewBindingDialog
import com.foundation.widget.loading.IPageLoading
import com.juziml.read.R
import com.juziml.read.core.ArchConfig
import com.juziml.read.core.base.loading.LoadingEventHelper
import com.juziml.read.core.base.loading.NormalLoadingAdapter
import com.juziml.read.core.net.BaseViewModel

/**
 * create by zhusw on 8/2/21 14:43
 */
abstract class ArchDialog<VB : ViewBinding>(
    activity: ComponentActivity,
    @StyleRes themeResId: Int = R.style.CustomDialog
) : BaseViewBindingDialog<VB>(activity, themeResId), LifecycleOwner, ViewModelStoreOwner {

    private val lifecycleRegistry by lazyAtomic { LifecycleRegistry(this) }

    val activityVMProvider by lazyAtomic { ViewModelProvider(activity) }
    val dialogVMProvider by lazyAtomic { ViewModelProvider(this) }

    private val dialogViewModelStore by lazyAtomic { ViewModelStore() }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    /**
     * 当你用Activity的vm时需要考虑如下：
     * 1.它会缓存到Activity上
     * 2.如果是Activity的同一个vm可能会互相影响
     * 3.注意observer的粘性问题
     */
    @MainThread
    inline fun <reified VM : ViewModel> lazyActivityVM(): Lazy<VM> {
        return AFViewModelLazy(VM::class, object : Function0<ViewModelProvider> {
            override fun invoke(): ViewModelProvider {
                return activityVMProvider
            }
        })
    }

    /**
     * 和普通写法一样，不需要考虑observer粘性问题
     */
    @MainThread
    inline fun <reified VM : ViewModel> lazyDialogVM(): Lazy<VM> {
        return AFViewModelLazy(VM::class, object : Function0<ViewModelProvider> {
            override fun invoke(): ViewModelProvider {
                return dialogVMProvider
            }
        })
    }

    override fun show() {
        //create是在之后，无法用create状态判断
        if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            if (ArchConfig.debug) {
                throw RuntimeException("无法show，当前state：${activity.lifecycle.currentState}")
            }
            return
        }
        super.show()
    }

    override fun initData() {
    }

    @CallSuper
    override fun onDestroyDialog() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }


    @CallSuper
    override fun onDismiss() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @CallSuper
    override fun onShow() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getViewModelStore(): ViewModelStore {
        return dialogViewModelStore
    }

    fun bindInitLoadingEvent(
        vm: BaseViewModel,
        loadingView: IPageLoading,
        onClickRetry: (() -> Unit)? = null
    ) {
        LoadingEventHelper.bindLoadingEvent(
            owner = this,
            liveData = vm.initLoadingLiveData,
            adapter = NormalLoadingAdapter(),
            loadingView = loadingView,
            onClickRetry = onClickRetry
        )
    }
}