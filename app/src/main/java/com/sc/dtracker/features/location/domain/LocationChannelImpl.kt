package com.sc.dtracker.features.location.domain

import com.sc.dtracker.features.location.domain.models.LocationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LocationChannelImpl : LocationChannelOutput, LocationChannelInput {

    private val sharedFlow = MutableSharedFlow<LocationState>(replay = 1, extraBufferCapacity = 1)

    override suspend fun setCurrentLocationState(state: LocationState) {
        sharedFlow.emit(state)
    }

    override fun observeLocationState(): Flow<LocationState> {
        return sharedFlow
    }
}