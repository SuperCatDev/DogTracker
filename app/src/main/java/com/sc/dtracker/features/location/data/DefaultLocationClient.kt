package com.sc.dtracker.features.location.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.sc.dtracker.common.ext.android.hasLocationPermission
import com.sc.dtracker.features.location.domain.LocationClient
import com.sc.dtracker.features.location.domain.LocationClient.LocationException
import com.sc.dtracker.features.location.domain.models.Location
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMs: Long): Flow<Location> {
        return callbackFlow {
            validateLocationAvailability(context)

            val request = getLocationRequest(intervalMs)
            val locationCallback = getLocationCallback()

            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun getLocationRequest(intervalMs: Long): LocationRequest {
        return LocationRequest.Builder(/* intervalMillis = */ intervalMs)
            .setWaitForAccurateLocation(true)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    private fun ProducerScope<Location>.getLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                result.lastLocation?.let { location ->
                    launch {
                        send(
                            Location(
                                longitude = location.longitude,
                                latitude = location.latitude,
                            )
                        )
                    }
                }
            }
        }
    }

    @Throws(LocationException::class)
    private fun validateLocationAvailability(context: Context) {
        if (!context.hasLocationPermission()) {
            throw LocationException(LocationException.MISSING_PERMISSION_EXCEPTION)
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            throw LocationException(LocationException.GPS_IS_DISABLED)
        }
    }
}