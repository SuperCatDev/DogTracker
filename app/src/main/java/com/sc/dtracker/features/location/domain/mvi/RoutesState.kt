package com.sc.dtracker.features.location.domain.mvi

import com.sc.dtracker.features.location.domain.models.RouteModel

data class RoutesState(
    val recordingRoute: RouteModel?,
    val staticRoutes: List<RouteModel>
) {
    fun isRecording() = recordingRoute != null
}

