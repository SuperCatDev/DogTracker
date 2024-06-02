package com.sc.dtracker.features.map.ui

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.sc.dtracker.R
import com.sc.dtracker.common.ext.android.asDp
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.features.map.domain.MapBehaviourStateHolder
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.sc.dtracker.ui.theme.isDarkThemeInCompose
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider

interface MapViewHost {

    fun provideMapViewContainer(): MapViewContainer
}


class MapViewContainer(
    context: Context,
    private val stateHolder: MapBehaviourStateHolder
) {

    private val locationView by lazyUnsafe {
        ImageView(context)
            .also {
                val size = 25.asDp()
                it.layoutParams = FrameLayout.LayoutParams(
                    size,
                    size,
                )
                it.setThemeNavigationIconResource()
            }
    }

    private var locationPlacemark: PlacemarkMapObject? = null

    private val mapView by lazyUnsafe {
        MapView(context).also {
            it.mapWindow.map.logo.setAlignment(
                Alignment(
                    HorizontalAlignment.RIGHT,
                    VerticalAlignment.BOTTOM
                )
            )

            stateHolder.globalRestoreCameraPos?.let { cp ->
                it.mapWindow.map.move(
                    cp
                )
            } ?: run {
                it.mapWindow.map.move(
                    CameraPosition(
                        Point(0.0, 0.0),
                        /* zoom = */ 17.0f,
                        /* azimuth = */ 150.0f,
                        /* tilt = */ 30.0f
                    )
                )
            }

            it.mapWindow.map.addCameraListener(LocationCameraListener())
        }
    }

    fun getView(): View {
        return mapView.also {
            if (it.mapWindow.map.isNightModeEnabled != isDarkThemeInCompose) {
                it.mapWindow.map.isNightModeEnabled = isDarkThemeInCompose
                locationView.setThemeNavigationIconResource()
            }
        }
    }

    fun onCreate() {
    }

    fun setLogoAt(horizontalPx: Int, verticalPx: Int) {
        mapView.mapWindow.map.logo.setPadding(
            Padding(horizontalPx, verticalPx)
        )
    }

    fun moveToLocation(location: MyLocation, azimuth: Float, withAnimation: Boolean) {
        moveMap(location, withAnimation)
        movePlacemark(location, azimuth)
    }

    private fun ImageView.setThemeNavigationIconResource() {
        setImageDrawable(
            ContextCompat.getDrawable(
                context,
                if (isDarkThemeInCompose) {
                    R.drawable.my_location_icon_dark
                } else {
                    R.drawable.my_location_icon_light
                }
            )
        )
    }

    private fun moveMap(location: MyLocation, withAnimation: Boolean) {
        if (withAnimation) {
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ mapView.mapWindow.map.cameraPosition.zoom,
                    /* azimuth = */ mapView.mapWindow.map.cameraPosition.azimuth,
                    /* tilt = */ mapView.mapWindow.map.cameraPosition.tilt
                ),
                Animation(Animation.Type.LINEAR, 1f),
                null
            )
        } else {
            mapView.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ mapView.mapWindow.map.cameraPosition.zoom,
                    /* azimuth = */ mapView.mapWindow.map.cameraPosition.azimuth,
                    /* tilt = */ mapView.mapWindow.map.cameraPosition.tilt
                )
            )
        }
    }

    private fun movePlacemark(location: MyLocation, azimuth: Float) {
        locationPlacemark?.let {
            it.geometry = Point(
                location.latitude,
                location.longitude,
            )
            it.direction = azimuth
        } ?: run {
            mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                setView(
                    ViewProvider(locationView, true),
                    IconStyle()
                        .apply {
                            rotationType = RotationType.ROTATE
                        }
                )
                geometry = Point(
                    location.latitude,
                    location.longitude,
                )
                direction = azimuth
                locationPlacemark = this
            }
        }
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onStop() {
        mapView.onStop()
    }

    private inner class LocationCameraListener : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            cameraPosition: CameraPosition,
            updateReson: CameraUpdateReason,
            changed: Boolean
        ) {
            if (changed) {
                stateHolder.globalRestoreCameraPos = cameraPosition
            }
        }
    }
}