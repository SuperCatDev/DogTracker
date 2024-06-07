package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.domain.LocationClient
import com.sc.dtracker.features.location.domain.models.MyLocation

sealed interface MapState {

    val viewAzimuth: Float

    data class NoLocation(
        override val viewAzimuth: Float
    ) : MapState

    data class WithLocation(
        override val viewAzimuth: Float,
        val location: MyLocation,
        val error: MapStateError = MapStateError.NoError,
        val followLocation: Boolean = true
    ) : MapState
}

sealed interface MapStateError {

    data object NoError : MapStateError
    data class Exists(val exception: LocationClient.LocationException) : MapStateError
}