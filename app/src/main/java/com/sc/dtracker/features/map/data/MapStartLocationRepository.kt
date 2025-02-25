package com.sc.dtracker.features.map.data

import com.sc.dtracker.features.location.data.LocationStorage
import com.sc.dtracker.features.location.domain.models.Location
import java.util.concurrent.atomic.AtomicBoolean

class MapStartLocationRepository(
    private val locationStorage: LocationStorage
) {

    private val consumed = AtomicBoolean(false)

    suspend fun getAndConsume(): Location? {
        return if (!consumed.getAndSet(true)) {
            locationStorage.getLastLocation()
        } else {
            null
        }
    }
}