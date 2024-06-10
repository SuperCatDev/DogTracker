package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.domain.LocationClient
import com.sc.dtracker.features.location.domain.models.Location

sealed interface MapState {

    val viewAzimuth: Float
    val trackRecording: Boolean
    val followLocation: Boolean

    data class NoLocation(
        override val viewAzimuth: Float,
        override val followLocation: Boolean = true,
        override val trackRecording: Boolean = false,
    ) : MapState

    data class WithLocation(
        override val viewAzimuth: Float,
        val location: Location,
        val error: MapStateError = MapStateError.NoError,
        override val followLocation: Boolean = true,
        override val trackRecording: Boolean = false,
    ) : MapState
}

fun MapState.copyCommon(
    viewAzimuth: Float = this.viewAzimuth,
    trackRecording: Boolean = this.trackRecording,
    followLocation: Boolean = this.followLocation,
): MapState {
    return when (this) {
        is MapState.NoLocation -> {
            this.copy(
                viewAzimuth = viewAzimuth,
                followLocation = followLocation,
                trackRecording = trackRecording
            )
        }
        is MapState.WithLocation -> {
            this.copy(
                viewAzimuth = viewAzimuth,
                followLocation = followLocation,
                trackRecording = trackRecording
            )
        }
    }
}

sealed interface MapStateError {

    data object NoError : MapStateError
    data class Exists(val exception: LocationClient.LocationException) : MapStateError
}