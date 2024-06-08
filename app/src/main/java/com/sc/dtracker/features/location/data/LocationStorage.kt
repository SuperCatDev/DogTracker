package com.sc.dtracker.features.location.data

import com.sc.dtracker.features.location.domain.models.Location

interface LocationStorage {

    suspend fun saveLastLocation(location: Location)
    suspend fun getLastLocation(defaultLocation: Location? = null): Location?

}