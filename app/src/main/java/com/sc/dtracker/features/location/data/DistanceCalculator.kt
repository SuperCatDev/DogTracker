package com.sc.dtracker.features.location.data

import com.sc.dtracker.features.location.domain.models.RouteModel

interface DistanceCalculator {

    suspend fun calculateDistanceMeters(route: RouteModel): Float
}