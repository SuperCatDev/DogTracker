package com.sc.dtracker.features.map.ui

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.sc.dtracker.R
import com.sc.dtracker.common.ext.android.asDp
import com.sc.dtracker.features.location.domain.models.Location
import com.sc.dtracker.features.location.domain.mvi.RouteModel
import com.sc.dtracker.features.location.domain.mvi.RoutesFeature
import com.sc.dtracker.features.location.domain.mvi.RoutesState
import com.sc.dtracker.features.map.domain.MapRestoreStateHolder
import com.sc.dtracker.features.map.domain.mvi.MapFeature
import com.sc.dtracker.features.map.domain.mvi.MapSideEffect
import com.sc.dtracker.features.map.domain.mvi.MapState
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.sc.dtracker.ui.theme.isDarkThemeInCompose
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.PolylineBuilder
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
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

interface MapViewHost {

    fun provideMapViewContainer(): MapViewContainer
}

class MapViewContainer(
    context: Context,
    private val mapFeature: MapFeature,
    routesFeature: RoutesFeature,
    coroutineScope: CoroutineScope,
    private val stateHolder: MapRestoreStateHolder
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

    private val locationMapListener = LocationCameraListener()
    private var locationPlacemark: PlacemarkMapObject? = null
    private val mapRoutePolylines = mutableMapOf<Int, WeakReference<PolylineMapObject>>()

    private val mapView by lazyUnsafe {
        MapView(context).also {
            it.mapWindow.map.logo.setAlignment(
                Alignment(
                    HorizontalAlignment.LEFT,
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
                        /* zoom = */ DEFAULT_ZOOM,
                        /* azimuth = */ 150.0f,
                        /* tilt = */ 30.0f
                    )
                )
            }

            it.mapWindow.map.addCameraListener(locationMapListener)
        }
    }

    init {
        mapFeature
            .container
            .stateFlow
            .onEach(::collectMapState)
            .launchIn(coroutineScope)

        mapFeature
            .container
            .sideEffectFlow
            .onEach(::collectMapSideEffect)
            .launchIn(coroutineScope)

        routesFeature
            .container
            .stateFlow
            .onEach(::collectRoutesState)
            .launchIn(coroutineScope)
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onStop() {
        mapView.onStop()
    }


    fun onCreate() {
    }

    fun getView(): View {
        return mapView.also {
            if (it.mapWindow.map.isNightModeEnabled != isDarkThemeInCompose) {
                it.mapWindow.map.isNightModeEnabled = isDarkThemeInCompose
                locationView.setThemeNavigationIconResource()
            }
        }
    }

    fun setLogoAt(horizontalPx: Int, verticalPx: Int) {
        mapView.mapWindow.map.logo.setPadding(
            Padding(horizontalPx, verticalPx)
        )
    }

    private fun collectMapState(state: MapState) {
        // todo probably isn't needed
        when (state) {
            is MapState.NoLocation -> {
            }
            is MapState.WithLocation -> {
            }
        }
    }


    private fun collectMapSideEffect(effect: MapSideEffect) {
        when (effect) {
            is MapSideEffect.MapMove -> {
                moveMap(
                    location = effect.location,
                    withAnimation = effect.animated,
                    azimuth = effect.azimuth,
                    zoom = effect.zoom,
                    tilt = effect.tilt,
                )
            }
            is MapSideEffect.UserMove -> {
                movePlacemark(
                    location = effect.location,
                    azimuth = effect.azimuth,
                )
            }
        }
    }

    private fun collectRoutesState(state: RoutesState) {
        state.staticRoutes.forEach {
            if (!mapRoutePolylines.contains(it.id)) {
                addOrUpdatePolyLine(it)
            }
        }

        state.recordingRoute?.let {
            addOrUpdatePolyLine(it)
        }
    }

    private fun addOrUpdatePolyLine(routeModel: RouteModel) {
        val newGeometry = PolylineBuilder().apply {
            routeModel.points.forEach { point ->
                append(Point(point.latitude, point.longitude))
            }
        }
            .build()

        val existedPolyline = mapRoutePolylines[routeModel.id]?.get()

        if (existedPolyline != null) {
            existedPolyline.geometry = newGeometry
        } else {
            mapView.mapWindow.map.mapObjects.addPolyline().apply {
                geometry = newGeometry
                strokeWidth = 6f
                turnRadius = 2f
                // todo wtf with int/long? prbbl color in int?
               // setStrokeColor(routeModel.color.toInt())
                setStrokeColor(ContextCompat.getColor(mapView.context, R.color.purple_200))
                mapRoutePolylines[routeModel.id] = WeakReference(this)
            }
        }
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

    private fun moveMap(
        location: Location,
        withAnimation: Boolean,
        azimuth: Float?,
        zoom: Float?,
        tilt: Float?,
    ) {
        if (withAnimation) {
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */
                    zoom ?: mapView.mapWindow.map.cameraPosition.zoom,
                    /* azimuth = */
                    azimuth ?: mapView.mapWindow.map.cameraPosition.azimuth,
                    /* tilt = */
                    tilt ?: mapView.mapWindow.map.cameraPosition.tilt
                ),
                Animation(Animation.Type.LINEAR, 0.5f),
            ) { finished ->
                if (finished) {
                    // priorityAnimating = false
                }
            }

            //   if (withUserPriority) {
            //       priorityAnimating = true
            //    }

        } else {
            mapView.map.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */
                    zoom ?: mapView.mapWindow.map.cameraPosition.zoom,
                    /* azimuth = */
                    azimuth ?: mapView.mapWindow.map.cameraPosition.azimuth,
                    /* tilt = */
                    tilt ?: mapView.mapWindow.map.cameraPosition.tilt
                )
            )
            //  priorityAnimating = false
        }
    }

    private fun movePlacemark(location: Location, azimuth: Float) {
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

    private inner class LocationCameraListener : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            cameraPosition: CameraPosition,
            updateReson: CameraUpdateReason,
            changed: Boolean
        ) {
            if (changed) {
                stateHolder.globalRestoreCameraPos = cameraPosition

                when (updateReson) {
                    CameraUpdateReason.GESTURES -> {
                        mapFeature.onMapMovedByUser()
                    }
                    CameraUpdateReason.APPLICATION -> {}
                }
            }
        }
    }

    private companion object {
        const val DEFAULT_ZOOM = 17.0f
    }
}