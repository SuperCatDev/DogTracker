package com.sc.dtracker.features.location.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.sc.dtracker.features.location.domain.LocationChannelInput
import com.sc.dtracker.features.location.domain.LocationClient
import com.sc.dtracker.features.location.domain.models.LocationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.android.ext.android.inject

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationController: LocationNotificationController by inject()
    private val locationClient: LocationClient by inject()
    private val locationInput: LocationChannelInput by inject()

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

        locationClient.getLocationUpdates(2000)
            .onStart {
                isLaunched = true
            }
            .onCompletion {
                isLaunched = false
            }
            .catch {
                when {
                    it is LocationClient.LocationException -> {
                        locationInput.setCurrentLocationState(
                            LocationState.Error(it)
                        )
                    }
                    else -> Log.e("LocationService", "Error: ${it.stackTraceToString()}")
                }
            }
            .onEach {
                locationInput.setCurrentLocationState(
                    LocationState.Value(it)
                )
            }
            .launchIn(serviceScope)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        var isLaunched: Boolean = false
            private set

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}