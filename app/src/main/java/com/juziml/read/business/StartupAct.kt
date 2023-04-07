package com.juziml.read.business

import android.content.Intent
import android.os.Bundle
import com.juziml.read.base.BaseActivity

class StartupAct : BaseActivity() {
    override fun bindData() {
    }

    override fun init(savedInstanceState: Bundle?) {
        startActivity(Intent(this, SimpleBookAct::class.java))
    }

}