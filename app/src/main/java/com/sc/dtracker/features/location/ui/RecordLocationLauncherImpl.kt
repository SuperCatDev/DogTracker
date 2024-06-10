package com.sc.dtracker.features.location.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordLocationLauncherImpl : RecordLocationLauncher {

    private val locationStartedStateFlow = MutableStateFlow(isStarted())

    private fun isStarted(): Boolean {
        // todo remove service flag, migrate to some domain class that actually records the trail!
        return LocationService.isLaunched
    }

    override fun observeStarted(): StateFlow<Boolean> {
        return locationStartedStateFlow
    }

    override fun start(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (context as? Activity)?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                    ),
                    PERMISSION_REQUEST_CODE,
                )
            }
        }
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        context.startService(intent)

        locationStartedStateFlow.requireEmit(true)
    }

    override fun stop(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        context.startService(intent)

        locationStartedStateFlow.requireEmit(false)
    }


    @Suppress("OPT_IN_USAGE")
    private fun <T> MutableStateFlow<T>.requireEmit(value: T) {
        if (!tryEmit(value)) {
            GlobalScope.launch {
                emit(value)
            }
        }
    }

    private companion object {
        const val PERMISSION_REQUEST_CODE = 2142
    }
}