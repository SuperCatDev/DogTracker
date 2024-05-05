package com.sc.dtracker.features.location.domain

import com.sc.dtracker.features.location.domain.models.MyLocation
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(intervalMs: Long): Flow<MyLocation>

    class LocationException(message: String): Exception(message) {

        companion object {
            const val MISSING_PERMISSION_EXCEPTION = "MISSING_PERMISSION_EXCEPTION"
            const val GPS_IS_DISABLED = "GPS_IS_DISABLED"
        }
    }
}