package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.data.SensorDataRepository
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.LocationController
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.features.map.data.MapStartLocationRepository
import com.sc.dtracker.features.map.domain.MapBehaviourStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.syntax.simple.runOn
import org.orbitmvi.orbit.syntax.simple.subIntent

@OptIn(OrbitExperimental::class)
class MapFeature(
    private val mapBehaviourStateHolder: MapBehaviourStateHolder,
    private val locationController: LocationController,
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

    fun onMapMovedByUser() {

    }

    fun onTrackingStarted() {

    }

    fun onJumpToCurrentLocation() {

    }

    private suspend fun observeAzimuthFlow() = subIntent {
        repeatOnSubscription {
            sensorDataRepository.getAzimuthFlow()
                .collect { azimuth ->
                    intent {
                        reduce {
                            when (val st = state) {
                                is MapState.NoLocation -> st.copy(viewAzimuth = azimuth)
                                is MapState.WithLocation -> st.copy(viewAzimuth = azimuth)
                            }
                        }
                    }
                }
        }
    }

    private suspend fun getLastAndStartObservingLocation() = subIntent {
        val initialLocation = mapStartLocationRepository.getAndConsume()

        runOn(MapState.NoLocation::class) {
            reduce {
                // todo send move map effects
                if (initialLocation != null) {
                    MapState.WithLocation(
                        location = initialLocation,
                        viewAzimuth = state.viewAzimuth
                    )
                } else {
                    MapState.WithLocation(
                        location = MyLocation(0.0, 0.0),
                        viewAzimuth = state.viewAzimuth
                    )
                }
            }

            observeLocation()
        }
    }

    private suspend fun observeLocation() = subIntent {
        repeatOnSubscription {
            locationOutput.observeLocationState()
                .collect { locationState ->
                    intent {
                        runOn(MapState.WithLocation::class) {
                            // todo send move map effects
                            when (locationState) {
                                is LocationState.NoActive -> Unit
                                is LocationState.Error -> {
                                    reduce {
                                        state.copy(
                                            error = MapStateError.Exists(
                                                locationState.error
                                            )
                                        )
                                    }
                                }
                                is LocationState.Value -> {
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
}