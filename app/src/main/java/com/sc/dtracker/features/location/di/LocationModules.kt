package com.sc.dtracker.features.location.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sc.dtracker.features.location.data.DefaultLocationClient
import com.sc.dtracker.features.location.data.LocationStorage
import com.sc.dtracker.features.location.data.LocationStorageImpl
import com.sc.dtracker.features.location.data.SensorDataRepository
import com.sc.dtracker.features.location.domain.LocationChannelImpl
import com.sc.dtracker.features.location.domain.LocationChannelInput
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.LocationClient
import com.sc.dtracker.features.location.ui.LocationLauncher
import com.sc.dtracker.features.location.ui.LocationLauncherImpl
import com.sc.dtracker.features.location.ui.LocationNotificationController
import com.sc.dtracker.features.location.ui.LocationNotificationControllerImpl
import org.koin.dsl.binds
import org.koin.dsl.module

val Context.locationDataStore: DataStore<Preferences> by preferencesDataStore(name = "location_store")

val locationDataModule = module {
    single<LocationClient> { DefaultLocationClient(get(), get()) }
    single<LocationStorage> { LocationStorageImpl(get<Context>().locationDataStore) }
    factory<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(get<Context>())
    }
    single<SensorDataRepository> { SensorDataRepository(get())  }
}

val locationDomainModule = module {
    single {
        LocationChannelImpl()
    } binds (arrayOf(LocationChannelOutput::class, LocationChannelInput::class))
}

val locationUiModule = module {
    single<LocationNotificationController> { LocationNotificationControllerImpl() }
    single<LocationLauncher> { LocationLauncherImpl() }
}