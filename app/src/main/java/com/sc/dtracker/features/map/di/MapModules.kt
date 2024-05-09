package com.sc.dtracker.features.map.di

import com.sc.dtracker.features.map.data.MapStartLocationRepository
import org.koin.dsl.module

val mapDataModule = module {
    single { MapStartLocationRepository(get()) }
}