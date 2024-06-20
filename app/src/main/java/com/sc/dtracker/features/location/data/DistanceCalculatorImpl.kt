package com.sc.dtracker.features.location.data

import android.location.Location
import com.sc.dtracker.features.location.domain.models.RouteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class DistanceCalculatorImpl : DistanceCalculator {

    override suspend fun calculateDistanceMeters(route: RouteModel): Float {
        return withContext(Dispatchers.Default) {
            var totalDistance = 0f

            for (i in 0 until route.points.size - 1) {

                if (!isActive) return@withContext totalDistance

                val startPoint = route.points[i]
                val endPoint = route.points[i + 1]

                val startLocation = Location("").apply {
                    latitude = startPoint.latitude
                    longitude = startPoint.longitude
                }

                val endLocation = Location("").apply {
                    latitude = endPoint.latitude
                    longitude = endPoint.longitude
                }

                val distance = startLocation.distanceTo(endLocation)
                totalDistance += distance
            }

            totalDistance
        }
    }
}