package com.sc.dtracker.features.location.di


import com.sc.dtracker.features.location.data.DefaultLocationClient
import com.sc.dtracker.features.location.domain.LocationClient
import org.koin.dsl.module

val locationDataModule = module {
    single<LocationClient> { DefaultLocationClient(get(), get()) }
}