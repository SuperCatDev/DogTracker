package com.sc.dtracker.features.location.data

import com.sc.dtracker.features.location.domain.models.MyLocation

interface LocationStorage {

    suspend fun saveLastLocation(location: MyLocation)
    suspend fun getLastLocation(defaultLocation: MyLocation? = null): MyLocation?

}