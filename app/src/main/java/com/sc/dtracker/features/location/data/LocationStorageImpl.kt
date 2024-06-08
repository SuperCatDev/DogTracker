package com.sc.dtracker.features.location.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sc.dtracker.features.location.domain.models.Location
import kotlinx.coroutines.flow.first

class LocationStorageImpl(
    private val dataStore: DataStore<Preferences>
) : LocationStorage {

    override suspend fun saveLastLocation(location: Location) {
        dataStore.edit { settings ->
            settings[LAST_LOCATION_KEY] = location.toSerializedString()
        }
    }

    override suspend fun getLastLocation(defaultLocation: Location?): Location? {
        val locationFromCache = try {
            val settings = dataStore.data.first()
            settings[LAST_LOCATION_KEY]?.locationFromSerializedString()
        } catch (th: Throwable) {
            Log.e("LocationStorage", "Error getting last location: ${th.stackTraceToString()}")
            null
        }

        return locationFromCache ?: defaultLocation
    }

    private fun Location.toSerializedString(): String {
        return "LAT:${latitude}LONG:${longitude}"
    }

    private fun String.locationFromSerializedString(): Location {
        val longStart = indexOf("LONG:")

        val longitude = substring("LONG:".length + longStart).toDouble()
        val latitude = substring("LAT:".length, longStart).toDouble()


        return Location(longitude = longitude, latitude = latitude)
    }

    private companion object {
        val LAST_LOCATION_KEY = stringPreferencesKey("LAST_LOCATION_KEY")
    }
}