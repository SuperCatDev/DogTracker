package com.sc.dtracker.features.location.ui

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface RecordLocationLauncher {

    fun observeStarted(): StateFlow<Boolean>
    fun start(context: Context)
    fun stop(context: Context)
}