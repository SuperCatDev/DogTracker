package com.sc.dtracker.features.location.domain

import com.sc.dtracker.features.location.data.LocationStorage
import com.sc.dtracker.features.location.domain.models.LocationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart

class LocationChannelImpl(
    private val locationStorage: LocationStorage
) : LocationChannelOutput, LocationChannelInput {

    private val sharedFlow = MutableSharedFlow<LocationState>(replay = 1, extraBufferCapacity = 1)

    override suspend fun setCurrentLocationState(state: LocationState) {
        sharedFlow.emit(state)

        if (state is LocationState.Value) {
            locationStorage.saveLastLocation(state.location)
        }
    }

    override fun observeLocationState(): Flow<LocationState> {
        return sharedFlow
            .onStart {
                emit(
                    LocationState.Value(
                        locationStorage.getLastLocation()
                    )
                )
            }
    }
}