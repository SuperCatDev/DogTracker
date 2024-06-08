package com.sc.dtracker.features.map.di

import com.sc.dtracker.features.map.data.MapStartLocationRepository
import com.sc.dtracker.features.map.domain.MapRestoreStateHolder
import com.sc.dtracker.features.map.domain.mvi.MapFeature
import org.koin.dsl.module

val mapDataModule = module {
    single { MapStartLocationRepository(get()) }
}

val mapDomainModule = module {
    single { MapRestoreStateHolder() }

    single { MapFeature(get(), get(), get()) }
}