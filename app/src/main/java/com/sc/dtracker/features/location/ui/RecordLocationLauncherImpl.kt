package com.sc.dtracker.features.location.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat

class RecordLocationLauncherImpl : RecordLocationLauncher {

    override fun isStarted(context: Context): Boolean {
        // todo remove service flag, migrate to some domain class that actually records the trail!
        return LocationService.isLaunched
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
    }

    override fun stop(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        context.startService(intent)
    }

    private companion object {
        const val PERMISSION_REQUEST_CODE = 2142
    }
}