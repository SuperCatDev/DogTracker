package com.sc.dtracker.features.location.domain.mvi

data class RoutesState(
    val recordingRoute: RouteModel?,
    val staticRoutes: List<RouteModel>
) {
    fun isRecording() = recordingRoute != null
}

