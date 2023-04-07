package com.juziml.read.utils.ext

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.foundation.widget.utils.anim.MjValueAnimator
import com.foundation.widget.utils.ext.view.isTextBold
import com.foundation.widget.utils.ext.view.setTextColorRes
import com.foundation.widget.utils.ext.view.textSizePx
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.juziml.read.R

interface CustomTabViewConfig<T : View> {
    fun getCustomView(context: Context): T
    fun onTabInit(tabView: T, position: Int)
}

fun TabLayout.createMediator(
    vp: ViewPager2,
    onInit: (tab: TabLayout.Tab, position: Int) -> Unit
): TabLayoutMediator {
    return TabLayoutMediator(this, vp) { tab, pos ->
        onInit(tab, pos)
    }
}

fun <T : View> TabLayout.createMediatorByCustomTabView(
    vp: ViewPager2,
    config: CustomTabViewConfig<T>
): TabLayoutMediator {
    return TabLayoutMediator(this, vp) { tab, pos ->
        val tabView = config.getCustomView(tab.view.context)
        tab.customView = tabView
        config.onTabInit(tabView, pos)
    }
}

class FixedWidthTextTabView(context: Context) : FrameLayout(context) {
    /**
     * 用于测量时能获得最大尺寸,
     */
    val boundSizeTextView: TextView = TextView(context).apply {
        visibility = View.INVISIBLE
    }
    val dynamicSizeTextView: ScaleTexViewTabView = ScaleTexViewTabView(context).apply {
        gravity = Gravity.CENTER
        setTextColorRes(R.color.color_333333)
    }

    init {
        addView(boundSizeTextView)
        addView(dynamicSizeTextView)

    }
}

/**
 * @param scale 渐变效果，默认[defaultTextConfig]
 */
abstract class FixedWidthTextTabViewConfig(val scale: TextSelectConfig = defaultTextConfig) :
    CustomTabViewConfig<FixedWidthTextTabView> {
    abstract fun getText(position: Int): String
    abstract fun onVisibleTextViewInit(tv: TextView)
    final override fun onTabInit(tabView: FixedWidthTextTabView, position: Int) {
        val text = getText(position)
        tabView.boundSizeTextView.text = text
        tabView.boundSizeTextView.textSizePx = scale.onSelectTextSize
        tabView.boundSizeTextView.typeface = Typeface.DEFAULT_BOLD
        tabView.dynamicSizeTextView.text = text
        tabView.dynamicSizeTextView.textSizePx = scale.onUnSelectTextSize
        tabView.dynamicSizeTextView.setTextColor(scale.onUnSelectTextColor)
        onVisibleTextViewInit(tabView.dynamicSizeTextView)
    }

    final override fun getCustomView(context: Context): FixedWidthTextTabView {
        return FixedWidthTextTabView(context)
    }
}

data class TextSelectConfig(
    @Px val onSelectTextSize: Int,
    @Px val onUnSelectTextSize: Int,
    @ColorInt val onSelectedTextColor: Int = R.color.color_333333.toColorInt,
    @ColorInt val onUnSelectTextColor: Int = R.color.color_333333.toColorInt,
)

private val defaultTextConfig = TextSelectConfig(16.dp, 15.dp)

/**
 * 创建vp的关联器
 */
fun TabLayout.createTextMediator(
    vp: ViewPager2,
    config: FixedWidthTextTabViewConfig
): TabLayoutMediator {
    val mediator = createMediatorByCustomTabView(vp, config)
    addScaleAnim(config.scale)
    return mediator
}

/**
 * 创建关联器的简化版
 */
fun TabLayout.createTextMediator(
    vp: ViewPager2,
    texts: List<String>,
    config: TextSelectConfig = defaultTextConfig,
): TabLayoutMediator {
    return createTextMediator(vp, object : FixedWidthTextTabViewConfig(config) {
        override fun getText(position: Int) = texts[position]

        override fun onVisibleTextViewInit(tv: TextView) {
        }
    })
}

/**
 * 注释见下方
 */
@JvmOverloads
fun TabLayout.createTextMediatorAndAttach(
    owner: LifecycleOwner,
    vp: ViewPager2,
    texts: List<String>,
    config: TextSelectConfig = defaultTextConfig,
) {
    createTextMediatorAndAttach(owner, vp, config) { texts[it] }
}

/**
 * 创建关联器并立即attach
 * 如果有旧的或destroy了则自动detach掉
 * 注意：必须在setAdapter之后
 *
 * @param owner 建议fragment传[Fragment.getViewLifecycleOwner]
 */
@JvmOverloads
fun TabLayout.createTextMediatorAndAttach(
    owner: LifecycleOwner,
    vp: ViewPager2,
    config: TextSelectConfig = defaultTextConfig,
    textCallback: (position: Int) -> String,
) {
    //删除旧的
    val tag = R.id.tag_tabLayout_mediator
    (getTag(tag) as? TabLayoutMediator)?.detach()

    val mediator = createTextMediator(vp, object : FixedWidthTextTabViewConfig(config) {
        override fun getText(position: Int) = textCallback.invoke(position)

        override fun onVisibleTextViewInit(tv: TextView) {
        }
    })
    //attach后虽然用的是自定义view，但仍会inflate默认的布局，tab多了耗时增加不少（attach>populateTabsFromPagerAdapter>newTab>createTabView>setTab>update>inflateXxxView）
    //后续可以考虑自行设计mediator，不调用newTab自行new对象
    mediator.attach()
    //添加生命周期监听
    owner.lifecycle.addObserver { _, _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            mediator.detach()
        }
    }

    setTag(tag, mediator)
}

private fun TabLayout.addScaleAnim(config: TextSelectConfig = defaultTextConfig) {
    val key = R.id.tag_tabLayout_scale_ext
    val lastListener = getTag(key)
    if (lastListener != null) return
    val duration = 50L
    val listener = object : TabLayout.OnTabSelectedListener {
        private fun scaleTextSize(stv: ScaleTexViewTabView, size: Int, isBold: Boolean) {
            val animator = MjValueAnimator.ofInt(stv.textSize.toInt(), size)
            animator.duration = duration
            animator.addUpdateListener { _, textSize ->
                stv.textSizePx = textSize
            }
            animator.addListener(
                onStart = {
                    stv.skipRequestLayout = true
                },
                onEnd = {
                    /*最后做一下修正*/
                    stv.textSizePx = size
                    stv.isTextBold = isBold
                    stv.skipRequestLayout = false
                }
            )
            animator.startForSingleView(stv)
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                scaleTextSize(cusV.dynamicSizeTextView, config.onSelectTextSize, true)
                cusV.dynamicSizeTextView.setTextColor(config.onSelectedTextColor)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            val cusV = tab.customView
            if (cusV is FixedWidthTextTabView) {
                scaleTextSize(cusV.dynamicSizeTextView, config.onUnSelectTextSize, false)
                cusV.dynamicSizeTextView.setTextColor(config.onUnSelectTextColor)
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    }
    addOnTabSelectedListener(listener)
    setTag(key, listener)

}

fun TabLayout.addScaleTabByTextView(
    textList: List<String>,
    textColor: Int,
    scale: TextSelectConfig = defaultTextConfig
) {
    removeAllTabs()
    addScaleAnim(scale)
    textList.forEach { text ->
        val tab = newTab().apply {
            val tabView = FixedWidthTextTabView(view.context)
            customView = tabView
            tabView.boundSizeTextView.text = text
            tabView.boundSizeTextView.textSizePx = scale.onSelectTextSize
            tabView.boundSizeTextView.typeface = Typeface.DEFAULT_BOLD
            tabView.dynamicSizeTextView.text = text
            tabView.dynamicSizeTextView.textSizePx = scale.onUnSelectTextSize
            tabView.dynamicSizeTextView.setTextColor(textColor)
        }
        addTab(tab)
    }
}


class ScaleTexViewTabView(context: Context) : AppCompatTextView(context) {
    var skipRequestLayout = false
    override fun requestLayout() {
        if (!skipRequestLayout) {
            super.requestLayout()
        }
    }
}
