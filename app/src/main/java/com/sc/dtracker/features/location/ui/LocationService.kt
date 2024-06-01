package com.sc.dtracker.features.location.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sc.dtracker.features.location.domain.LocationController
import org.koin.android.ext.android.inject

class LocationService : Service() {

    private val locationController: LocationController by inject()
    private val notificationController: LocationNotificationController by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        startForeground(1, notificationController.buildServiceNotification(this))
        locationController.requestStart(FROM_KEY)
        isLaunched = true
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationController.requestStop(FROM_KEY)
        isLaunched = false
    }

    companion object {
        var isLaunched: Boolean = false
            private set

        const val FROM_KEY = "LocationService"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}