package com.sc.dtracker.features.location.ui

import android.content.Context
import android.content.Intent

class LocationLauncherImpl : LocationLauncher {

    override fun isStarted(context: Context): Boolean {
        return LocationService.isLaunched
    }

    override fun start(context: Context) {
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
}