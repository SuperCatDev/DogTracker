package com.sc.dtracker.common.ext.android

import android.content.Context
import android.util.DisplayMetrics
import com.sc.dtracker.common.context.AppContextHolder

fun Int.asDp(): Int {
    val density = displayMetrics().density
    return (this * density).toInt()
}

private fun displayMetrics(): DisplayMetrics {
    val context: Context = AppContextHolder.context
    val resources = context.resources
    return resources.displayMetrics
}