package com.sc.dtracker.features.map.domain.mvi

import com.sc.dtracker.features.location.domain.models.Location

sealed interface MapSideEffect {

    data class MapMove(
        val location: Location,
        val animated: Boolean,
        val azimuth: Float? = null,
        val zoom: Float? = null,
        val tilt: Float? = null,
    ): MapSideEffect

    data class UserMove(
        val location: Location,
        val azimuth: Float,
    ): MapSideEffect
}