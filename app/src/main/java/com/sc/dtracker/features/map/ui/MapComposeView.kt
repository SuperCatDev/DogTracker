package com.sc.dtracker.features.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import org.koin.compose.getKoin

@Composable
fun MapComposeView() {

    val locationOutput: LocationChannelOutput = getKoin().get()
    val locationState = locationOutput.observeLocationState()
        // todo use last location
        .collectAsState(initial = LocationState.Value(MyLocation(0.0, 0.0)))

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            (context as MapViewHost).provideMapViewContainer().getView()
        },
        update = { view ->
            when (val value = locationState.value) {
                is LocationState.Error -> {}
                is LocationState.Value -> {
                    view.map.move(
                        CameraPosition(
                            Point(value.location.latitude, value.location.longitude),
                            /* zoom = */ 17.0f,
                            /* azimuth = */ 150.0f,
                            /* tilt = */ 30.0f
                        )
                    )
                }
            }
        },
    )
}