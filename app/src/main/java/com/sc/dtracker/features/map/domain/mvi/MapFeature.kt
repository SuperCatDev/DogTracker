package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.data.SensorDataRepository
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.Location
import com.sc.dtracker.features.map.data.MapStartLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
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
    private val mapStartLocationRepository: MapStartLocationRepository
) : ContainerHost<MapState, MapSideEffect> {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val container: Container<MapState, MapSideEffect> = scope.container(
        MapState.NoLocation(0f)
    ) {
        coroutineScope {
            launch { getLastAndStartObservingLocation() }
            launch { observeAzimuthFlow() }
        }
    }

    fun onMapMovedByUser() = intent {
        runOn(MapState.WithLocation::class) {
            reduce { state.copy(followLocation = false) }
        }
    }

    // todo must return to follow user after some time and so we need to have onTrackingStarted/stopped callbacks
    fun onTrackingStarted() = intent {
        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.MapMove(
                    location = state.location,
                    animated = true,
                    zoom = DEFAULT_FOLLOW_ZOOM,
                )
            )
            reduce { state.copy(followLocation = true) }
        }
    }

    // todo currently the same as onTrackingStarted but prbbly should behave differently
    fun onJumpToCurrentLocation() = intent {
        runOn(MapState.WithLocation::class) {
            postSideEffect(
                MapSideEffect.MapMove(
                    location = state.location,
                    animated = true,
                    zoom = DEFAULT_FOLLOW_ZOOM,
                )
            )
            reduce { state.copy(followLocation = true) }
        }
    }

    private suspend fun observeAzimuthFlow() = subIntent {
        scope.launch {
            sensorDataRepository.getAzimuthFlow()
                .collect { azimuth ->
                    intent {
                        when (val st = state) {
                            is MapState.NoLocation -> {
                                reduce { st.copy(viewAzimuth = azimuth) }
                            }

                            is MapState.WithLocation -> {
                                onLocationChangedAzimuthEffect(azimuth)
                                reduce { st.copy(viewAzimuth = azimuth) }
                            }
                        }
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
                    viewAzimuth = state.viewAzimuth
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
    }
}