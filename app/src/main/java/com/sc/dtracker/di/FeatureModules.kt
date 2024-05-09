package com.sc.dtracker.di

import com.sc.dtracker.features.location.di.locationDataModule
import com.sc.dtracker.features.location.di.locationDomainModule
import com.sc.dtracker.features.location.di.locationUiModule
import com.sc.dtracker.features.map.di.mapDataModule
import org.koin.core.KoinApplication

fun KoinApplication.featureModules() {
    modules(
        // map feature
        mapDataModule,
        // location feature
        locationDataModule,
        locationDomainModule,
        locationUiModule,
    )
}
