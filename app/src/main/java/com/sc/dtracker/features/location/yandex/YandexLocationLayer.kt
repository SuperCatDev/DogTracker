package com.sc.dtracker.features.location.yandex

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sc.dtracker.R
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.CompositeIcon
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import java.lang.ref.WeakReference

class YandexLocationLayer : UserLocationObjectListener, CameraListener {

    private val handler by lazyUnsafe { Handler(Looper.getMainLooper()) }
    private val followRunnable = Runnable {
        setAnchor()
    }

    private var _userLocationLayer: UserLocationLayer? = null
    private val userLocationLayer: UserLocationLayer
        get() = _userLocationLayer!!

    private var _context: Context? = null
    private val context: Context
        get() = _context!!

    private var _mapView: WeakReference<MapView>? = null
    private val mapView: MapView
        get() = _mapView?.get()!!

    fun initialize(context: Context) {
        _context = context
        MapKitFactory.getInstance().resetLocationManagerToDefault()
    }

    fun clear() {
        handler.removeCallbacks(followRunnable)
        mapView.mapWindow.map.removeCameraListener(this)
        _context = null
        _userLocationLayer?.setObjectListener(null)
        _userLocationLayer = null
        _mapView = null
    }

    fun attachToMapView(mapView: MapView) {
        mapView.mapWindow.map.removeCameraListener(this)
        mapView.mapWindow.map.addCameraListener(this)

        _mapView = WeakReference(mapView)
        _userLocationLayer?.setObjectListener(null)
        _userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)

        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setDefaultSource()

        mapView.mapWindow.map.move(CameraPosition(Point(0.0, 0.0), 14.0f, 0.0f, 0.0f))
    }

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
        )
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        Log.e("VVV", "onObjectAdded: $userLocationView")
        setAnchor()

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context, R.drawable.user_arrow
            )
        )

        val pinIcon: CompositeIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(context, R.drawable.baseline_my_location_24),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {

    }

    override fun onObjectUpdated(locationView: UserLocationView, event: ObjectEvent) {

    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPos: CameraPosition,
        updateReason: CameraUpdateReason,
        p3: Boolean
    ) {
        if (updateReason == CameraUpdateReason.GESTURES) {
            userLocationLayer.resetAnchor()
            handler.removeCallbacks(followRunnable)
        }

        handler.postDelayed(followRunnable, 15000)
    }

}