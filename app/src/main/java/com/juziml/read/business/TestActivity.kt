package com.juziml.read.business

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.juziml.read.core.base.component.BaseActivity
import com.juziml.read.databinding.ActTestBinding
import com.juziml.read.utils.ext.typingAnimation

class TestActivity : BaseActivity() {
    private val vb by lazyVB<ActTestBinding>()
    override fun getContentVB(): ViewBinding? {
        return null
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun bindData() {
        setContentView(vb.root)
//        setContentView(R.layout.act_chat)
    }

    override fun init(savedInstanceState: Bundle?) {
        vb.btnTextAnim.setOnClickListener {
            vb.tat.typingAnimation(
                lifecycle,
                "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      " +
                        "正在录音...      正在录音...      正在录音...      " +
                        "正在录音...      " +
                        "" +
                        "", loop = false
            )

        }

        vb.eventView.setOnClickListener {
            println("eventView:click")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        println("eventView:click")
//        Thread.sleep(10*10000)
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        Thread.sleep(10 * 10000)
        super.onResume()
    }

    override fun onStop() {
//        Thread.sleep(10*10000)
        super.onStop()
    }
}

class EventView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }

}