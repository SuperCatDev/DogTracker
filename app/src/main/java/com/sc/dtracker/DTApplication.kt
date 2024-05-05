package com.sc.dtracker

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class DTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}