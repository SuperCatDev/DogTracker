package com.sc.dtracker.features.location.domain.mvi

import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.RoutesPalette
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.RouteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import kotlin.random.Random

class RoutesFeature(
    private val routesPalette: RoutesPalette,
    private val locationOutput: LocationChannelOutput,
) : ContainerHost<RoutesState, RoutesSideEffect> {

    private val randomizer = Random(hashCode())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val container: Container<RoutesState, RoutesSideEffect> = scope.container(
        RoutesState(null, emptyList())
    ) {
        // todo load saved routes
        observeLocation()
    }

    fun startNewRecord() = intent {
        reduce {
            val newStaticList = stopCurrentRecording(state)

            state.copy(
                recordingRoute = generateNewRecording(state),
                staticRoutes = newStaticList,
            )
        }
    }

    fun stopRecord() = intent {
        reduce {
            val newStaticList = stopCurrentRecording(state)

            state.copy(
                recordingRoute = null,
                staticRoutes = newStaticList,
            )
        }
    }

    private fun observeLocation() {
        scope.launch {
            locationOutput.observeLocationState()
                .collect { locationState ->
                    intent {
                        if (state.isRecording() && locationState is LocationState.Value) {
                            reduce {
                                val recordingRoute = state.recordingRoute ?: return@reduce state
                                val points = recordingRoute.points.toMutableList().also {
                                    it.add(
                                        locationState.location
                                    )
                                }

                                state.copy(
                                    recordingRoute = recordingRoute.copy(points = points)
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun stopCurrentRecording(currentState: RoutesState): List<RouteModel> {
        val activeRecording = currentState.recordingRoute
        return if (activeRecording != null) {
            saveRoute(activeRecording)
            currentState.staticRoutes.toMutableList().also {
                it.add(activeRecording)
            }
        } else {
            currentState.staticRoutes
        }
    }

    private fun saveRoute(routeModel: RouteModel) {
        // todo save active recording
    }

    private fun generateNewRecording(currentState: RoutesState): RouteModel {
        val id = currentState.staticRoutes.lastOrNull()?.id?.inc() ?: randomizer.nextInt()

        return RouteModel(
            id = id,
            color = routesPalette.getColorFromPaletteFor(id),
            name = "Record_$id",
            points = emptyList()
        )
    }
}