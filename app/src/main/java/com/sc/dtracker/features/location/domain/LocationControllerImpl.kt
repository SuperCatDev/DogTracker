package com.sc.dtracker.features.location.domain

import android.util.Log
import com.sc.dtracker.features.location.data.LocationStorage
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
import kotlinx.coroutines.isActive

class LocationControllerImpl(
    private val locationClient: LocationClient,
    private val locationInput: LocationChannelInput,
    private val locationStorage: LocationStorage,
) : LocationController {

    private val requested = mutableSetOf<String>()
    private var started: Boolean = false
    private var observeLocationScope = createScope()

    override fun requestStart(from: String) {
        requested.add(from)

        if (!started) {
            startObserveLocation()
        }
    }

    override fun requestStop(from: String) {
        requested.remove(from)

        if (!started) return

        if (requested.size == 0) {
            stopObserveLocation()
        }
    }

    private fun startObserveLocation() {
        if (!observeLocationScope.isActive) {
            observeLocationScope = createScope()
        }

        locationClient.getLocationUpdates(1000)
            .onStart {
                started = true
            }
            .onCompletion {
                started = false
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
                resetState()
            }
            .onEach {
                locationInput.setCurrentLocationState(
                    LocationState.Value(it)
                )
                locationStorage.saveLastLocation(it)
            }
            .onCompletion {
                locationInput.setCurrentLocationState(LocationState.NoActive)
            }
            .launchIn(observeLocationScope)
    }

    private fun resetState() {
        requested.clear()
        stopObserveLocation()
    }

    private fun stopObserveLocation() {
        observeLocationScope.cancel()
    }

    private fun createScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}