package com.juziml.read.utils.ext

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.juziml.read.R
import java.lang.Integer.min

fun View.animateScaleLoop(
    scale: Float,
    duration: Long,
    start: Boolean = true
): Animation {
    val animation = ScaleAnimation(
        1F, scale, 1F, scale, Animation.RELATIVE_TO_SELF,
        0.5F, Animation.RELATIVE_TO_SELF, 0.5F
    )
    animation.repeatCount = ScaleAnimation.INFINITE
    animation.duration = duration
    animation.repeatMode = ScaleAnimation.REVERSE
    animation.interpolator = LinearInterpolator()
    if (start) {
        this.startAnimation(animation)
    }
    return animation
}

fun View.animateRotateLoop(
    fromDegrees: Float = 0F,
    toDegrees: Float = 361F,
    duration: Long,
    start: Boolean = true
): Animation {
    val animation = RotateAnimation(
        fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF,
        0.5F, Animation.RELATIVE_TO_SELF, 0.5F
    )
    animation.duration = duration
    animation.repeatCount = ScaleAnimation.INFINITE
    animation.repeatMode = ScaleAnimation.INFINITE
    animation.interpolator = LinearInterpolator()
    if (start) {
        this.startAnimation(animation)
    }
    return animation
}

fun TextView.typingAnimation(
    lifecycle: Lifecycle,
    text: String,
    startIndex: Int = 0,
    loop: Boolean = false,
    maxDuration: Int = 10000,
    speed: Int = 200
) {
    val duration = min(maxDuration, text.length * speed).toLong()
    //取消上一个动画
    val animator =
        getTag(R.id.typing_animation_id_1) as? ObjectAnimator
            ?: ObjectAnimator.ofInt(
                startIndex,
                startIndex, text.length - 1
            )
    animator.setIntValues(startIndex, text.length - 1)
    setTag(R.id.typing_animation_id_1, animator)
    animator.duration = duration
    if (loop) {
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.RESTART
    } else {
        animator.interpolator = AccelerateInterpolator(1.3F)
    }
    animator.addUpdateListener {
        val value = it.animatedValue as Int
//        println("typeAnimaValue:$value")
        setText(text.subSequence(0, value))
    }
    val observer = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            if (animator.isRunning) {
                animator.pause()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            animator.resume()
        }
    }
    animator.doOnEnd {
        lifecycle.removeObserver(observer)
    }
    lifecycle.addObserver(observer)
    animator.start()
}

//下面是动态修改自定义控制进度fraction的例子
class TypingAnimationTextView(context: Context, attr: AttributeSet) :
    AppCompatTextView(context, attr) {

    var stagingText: String = ""
    var textIndex = 0
        set(value) {
            setText(stagingText.subSequence(0, value))
        }
    var typingAnimation: ObjectAnimator? = null
}

fun TypingAnimationTextView.typing(
    text: String,
    loop: Boolean = false,
    startIndex: Int = 0,
    speed: Int = 100,
    maxDuration: Int = 10000
) {
    typingAnimation?.let {
        if (it.isRunning) {
            it.cancel()
        }
    }
    stagingText = text
    val indexSize = stagingText.length - 1
    val duration = min(maxDuration, stagingText.length * speed).toLong()
    val evaluator = TypeEvaluator<Int> { fraction, startValue, endValue ->
        val result = startValue + (fraction * (endValue - startValue)).toInt()
        println("result:$result")
        result
    }
    val animator = ObjectAnimator.ofObject(
        this, "textIndex", evaluator, startIndex, indexSize
    )
    animator.duration = duration
    if (loop) {
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.RESTART
    } else {
        animator.interpolator = AccelerateInterpolator(1.3F)
    }
    animator.start()
}