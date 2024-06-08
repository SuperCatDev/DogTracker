package com.sc.dtracker.features.location.domain.models

import com.sc.dtracker.features.location.domain.LocationClient

sealed interface LocationState {

    data object NoActive: LocationState
    data class Value(val location: Location) : LocationState
    data class Error(val error: LocationClient.LocationException) : LocationState
}