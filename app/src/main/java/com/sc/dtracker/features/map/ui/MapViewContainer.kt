package com.sc.dtracker.features.map.ui

import android.content.Context
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

interface MapViewHost {

    fun provideMapViewContainer(): MapViewContainer
}

class MapViewContainer(context: Context) {

    private val mapView by lazyUnsafe {
        MapView(context).also {
            it.map.logo.setAlignment(
                Alignment(
                    HorizontalAlignment.RIGHT,
                    VerticalAlignment.BOTTOM
                )
            )
        }
    }

    fun getView(): MapView {
        return mapView
    }

    fun onCreate() {

    }

    fun setLogoAt(horizontalPx: Int, verticalPx: Int) {
        mapView.map.logo.setPadding(
            Padding(horizontalPx, verticalPx)
        )
    }

    fun moveToLocation(location: MyLocation) {
        mapView.map.move(
            CameraPosition(
                Point(location.latitude, location.longitude),
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