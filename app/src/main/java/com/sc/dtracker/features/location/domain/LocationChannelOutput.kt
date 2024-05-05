package com.sc.dtracker.features.location.domain

import com.sc.dtracker.features.location.domain.models.LocationState
import kotlinx.coroutines.flow.Flow

interface LocationChannelOutput {

    fun observeLocationState(): Flow<LocationState>
}