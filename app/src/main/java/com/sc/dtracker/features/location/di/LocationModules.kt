package com.sc.dtracker.features.location.di


import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sc.dtracker.features.location.data.DefaultLocationClient
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

val locationDataModule = module {
    single<LocationClient> { DefaultLocationClient(get(), get()) }
    factory<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(get<Context>())
    }
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