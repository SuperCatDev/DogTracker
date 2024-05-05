package com.sc.dtracker.features.map.ui

import android.content.Context
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

interface MapViewHost {

    fun provideMapViewContainer(): MapViewContainer
}

class MapViewContainer(context: Context) {

    private val mapView by lazyUnsafe {
        MapView(context)
    }

    fun getView(): MapView {
        return mapView
    }

    fun onCreate() {
        val map = mapView.map
        map.move(
            CameraPosition(
                Point(55.751225, 37.629540),
                /* zoom = */ 17.0f,
                /* azimuth = */ 150.0f,
                /* tilt = */ 30.0f
            )
        )
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onStop() {
        mapView.onStop()
    }

}