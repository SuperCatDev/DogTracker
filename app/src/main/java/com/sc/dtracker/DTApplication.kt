package com.sc.dtracker

import android.app.Application
import com.sc.dtracker.common.context.AppContextHolder
import com.sc.dtracker.di.featureModules
import com.sc.dtracker.features.location.ui.LocationNotificationController
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContextHolder.context = this

        startDI()

        get<LocationNotificationController>()
            .registerNotificationChannel(this)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    private fun startDI() {
        startKoin {
            androidContext(this@DTApplication)
            androidLogger()
            featureModules()
        }
    }
}