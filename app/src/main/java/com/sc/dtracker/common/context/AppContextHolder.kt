package com.sc.dtracker.common.context

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextHolder {
    lateinit var context: Context

    fun isInitialized(): Boolean = ::context.isInitialized
}