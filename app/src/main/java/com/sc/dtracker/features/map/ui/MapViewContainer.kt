package com.sc.dtracker.features.map.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.sc.dtracker.R
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

interface MapViewHost {

    fun provideMapViewContainer(): MapViewContainer
}

class MapViewContainer(context: Context) {

    private val locationBitmap by lazyUnsafe {
        context.getBitmapFromVectorDrawable(R.drawable.baseline_my_location_24)
    }

    private var locationPlacemark: PlacemarkMapObject? = null

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

    fun moveToLocation(location: MyLocation, withAnimation: Boolean) {
        moveMap(location, withAnimation)
        movePlacemark(location)
    }

    private fun moveMap(location: MyLocation, withAnimation: Boolean) {
        if (withAnimation) {
            mapView.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 17.0f,
                    /* azimuth = */ 150.0f,
                    /* tilt = */ 30.0f
                ),
                Animation(Animation.Type.LINEAR, 1f),
                null
            )
        } else {
            mapView.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 17.0f,
                    /* azimuth = */ 150.0f,
                    /* tilt = */ 30.0f
                )
            )
        }
    }

    private fun movePlacemark(location: MyLocation) {
        locationPlacemark?.let {
            it.geometry = Point(
                location.latitude,
                location.longitude,
            )
        } ?: run {
            mapView.map.mapObjects.addPlacemark().apply {
                geometry = Point(
                    location.latitude,
                    location.longitude,
                )
                setIcon(
                    ImageProvider.fromBitmap(locationBitmap)
                )
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

    private fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}