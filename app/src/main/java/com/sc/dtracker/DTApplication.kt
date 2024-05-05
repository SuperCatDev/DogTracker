package com.sc.dtracker

import android.app.Application
import com.sc.dtracker.features.location.di.locationDataModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DTApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DTApplication)
            modules(
                locationDataModule
            )
        }

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}