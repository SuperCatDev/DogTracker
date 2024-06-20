package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.data.SensorDataRepository
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.models.Location
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.map.data.MapStartLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitDsl
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.runOn
import org.orbitmvi.orbit.syntax.simple.subIntent

@OptIn(OrbitExperimental::class)
class MapFeature(
    private val locationOutput: LocationChannelOutput,
    private val sensorDataRepository: SensorDataRepository,
    private val recordLocationStartedState: StateFlow<Boolean>,
    private val mapStartLocationRepository: MapStartLocationRepository
) : ContainerHost<MapState, MapSideEffect> {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var returnToUserTrackingJob: Job? = null

    override val container: Container<MapState, MapSideEffect> = scope.container(
        MapState.NoLocation(0f)
    ) {
        coroutineScope {
            launch { getLastAndStartObservingLocation() }
            launch { observeAzimuthFlow() }
            launch { observeRecordLocationStarted() }
        }
    }

    fun onMapMovedByUser() = intent {
        reduce { state.copyCommon(followLocation = false) }
        maybeScheduleUserFollowing()
    }

    fun onJumpToCurrentLocation() = intent {
        reduce { state.copyCommon(followLocation = true) }

        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.MapMove(
                    location = state.location,
                    animated = true,
                    zoom = DEFAULT_FOLLOW_ZOOM,
                )
            )
        }
    }

    private fun maybeScheduleUserFollowing() = intent {
        returnToUserTrackingJob?.cancel()
        returnToUserTrackingJob = null

        if (state.trackRecording) {
            returnToUserTrackingJob = scope.launch {
                delay(INTERVAL_BEFORE_RETURN_TO_LOCATION)
                intent {
                    reduce {
                        state.copyCommon(followLocation = true)
                    }
                }
            }
        }
    }

    private suspend fun observeRecordLocationStarted() = subIntent {
        scope.launch {
            recordLocationStartedState.collect { started ->
                if (started) {
                    onTrackingStarted()
                } else {
                    onTrackingStopped()
                }
            }
        }
    }

    private suspend fun observeAzimuthFlow() = subIntent {
        scope.launch {
            sensorDataRepository.getAzimuthFlow()
                .collect { azimuth ->
                    intent {
                        reduce { state.copyCommon(viewAzimuth = azimuth) }
                        onLocationChangedAzimuthEffect(azimuth)
                    }
                }
        }
    }

    private suspend fun getLastAndStartObservingLocation() = subIntent {
        val initialLocation = mapStartLocationRepository.getAndConsume() ?: Location(0.0, 0.0)

        runOn(MapState.NoLocation::class) {
            postSideEffect(
                MapSideEffect.MapMove(
                    location = initialLocation,
                    animated = false,
                    azimuth = 0f
                )
            )

            reduce {
                MapState.WithLocation(
                    location = initialLocation,
                    viewAzimuth = state.viewAzimuth,
                    followLocation = state.followLocation,
                    trackRecording = state.trackRecording,
                )
            }

            observeLocation()
        }
    }

    private suspend fun observeLocation() = subIntent {
        scope.launch {
            locationOutput.observeLocationState()
                .collect { locationState ->

                    intent {
                        when (locationState) {
                            is LocationState.NoActive -> Unit
                            is LocationState.Error -> {
                                runOn(MapState.WithLocation::class) {
                                    reduce {
                                        state.copy(
                                            error = MapStateError.Exists(
                                                locationState.error
                                            )
                                        )
                                    }
                                }
                            }
                            is LocationState.Value -> {
                                onLocationChangedEffect(locationState.location)
                                runOn(MapState.WithLocation::class) {
                                    reduce {
                                        state.copy(
                                            location = locationState.location,
                                            error = MapStateError.NoError
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun onTrackingStarted() = intent {
        reduce {
            state.copyCommon(
                followLocation = true,
                trackRecording = true
            )
        }

        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.MapMove(
                    location = state.location,
                    animated = true,
                    zoom = DEFAULT_FOLLOW_ZOOM,
                )
            )
        }
    }

    private fun onTrackingStopped() = intent {
        returnToUserTrackingJob?.cancel()
        reduce { state.copyCommon(trackRecording = false) }
    }

    @OrbitDsl
    private suspend fun SimpleSyntax<MapState, MapSideEffect>.onLocationChangedAzimuthEffect(
        azimuth: Float
    ) {
        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.UserMove(
                    location = state.location,
                    azimuth = azimuth,
                )
            )

            if (state.followLocation) {
                postSideEffect(
                    MapSideEffect.MapMove(
                        location = state.location,
                        animated = true,
                        zoom = DEFAULT_FOLLOW_ZOOM,
                    )
                )
            }
        }
    }

    @OrbitDsl
    private suspend fun SimpleSyntax<MapState, MapSideEffect>.onLocationChangedEffect(
        location: Location
    ) {
        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.UserMove(
                    location = location,
                    azimuth = state.viewAzimuth,
                )
            )

            if (state.followLocation) {
                postSideEffect(
                    MapSideEffect.MapMove(
                        location = location,
                        animated = true,
                        zoom = DEFAULT_FOLLOW_ZOOM,
                    )
                )
            }
        }
    }

    private companion object {
        const val DEFAULT_FOLLOW_ZOOM = 17.0f
        const val INTERVAL_BEFORE_RETURN_TO_LOCATION = 15_000L
    }
}