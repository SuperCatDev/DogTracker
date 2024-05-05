package com.sc.dtracker.features.location.domain

import com.sc.dtracker.features.location.domain.models.LocationState

interface LocationChannelInput {

    suspend fun setCurrentLocationState(state: LocationState)
}