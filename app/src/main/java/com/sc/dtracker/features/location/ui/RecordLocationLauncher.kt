package com.sc.dtracker.features.location.ui

import android.content.Context

interface RecordLocationLauncher {

    fun isStarted(context: Context): Boolean
    fun start(context: Context)
    fun stop(context: Context)
}