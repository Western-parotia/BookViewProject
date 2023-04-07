package com.juziml.read.base

import android.os.Bundle
import com.foundation.app.arc.activity.BaseFragmentManagerActivity

abstract class BaseActivity : BaseFragmentManagerActivity() {
    override fun afterSuperOnCreate(savedInstanceState: Bundle?) {
    }

    override fun beforeSuperOnCreate(savedInstanceState: Bundle?) {
    }

    override fun initViewModel() {

    }
}