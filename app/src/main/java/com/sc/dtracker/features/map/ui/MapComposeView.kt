package com.sc.dtracker.features.map.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sc.dtracker.features.location.data.SensorDataRepository
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.features.map.data.MapStartLocationRepository
import com.sc.dtracker.ui.views.bottomNavBarCornerHeight
import com.sc.dtracker.ui.views.bottomNavBarHeight
import org.koin.compose.getKoin

private fun Context.asMapViewContainer() = (this as MapViewHost).provideMapViewContainer()

@Composable
fun MapComposeView() {

    val locationOutput: LocationChannelOutput = getKoin().get()
    val sensorDataRepository: SensorDataRepository = getKoin().get()
    val locationState = locationOutput.observeLocationState()
        .collectAsState(initial = LocationState.NoActive)

    var azimuth by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(sensorDataRepository) {
        sensorDataRepository.getAzimuthFlow().collect { newAzimuth ->
            azimuth = newAzimuth
        }
    }

    // todo probably move to view update area
    MoveMap(locationState.value, azimuth)
    UpdateLogo(bottomNavBarCornerHeight)

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomNavBarHeight),
        factory = { context ->
            context.asMapViewContainer().getView()
        },
        update = {
        }
    )
}

@Composable
private fun MoveMap(
    locationState: LocationState,
    azimuth: Float
) {
    val mapStartLocationRepository: MapStartLocationRepository = getKoin().get()

    val initialLocationState = remember {
        mutableStateOf<MyLocation?>(null)
    }

    LaunchedEffect(key1 = Unit) {
        initialLocationState.value = mapStartLocationRepository.getAndConsume()
    }

    val ctx = LocalContext.current

    when (locationState) {
        is LocationState.Error -> {}
        is LocationState.Value -> {
            ctx.asMapViewContainer().moveToLocation(locationState.location, azimuth, true)
        }
        is LocationState.NoActive -> {
            initialLocationState.value?.let {
                ctx.asMapViewContainer().moveToLocation(it, azimuth, false)
            }

            initialLocationState.value = null
        }
    }
}

@Composable
private fun UpdateLogo(
    bottomPadding: Dp,
) {
    val ctx = LocalContext.current
    val density = LocalDensity.current

    val rightPadding = 8.dp.value * density.density
    val logoBottomPadding = (bottomPadding.value) * density.density

    ctx.asMapViewContainer()
        .setLogoAt(rightPadding.toInt(), logoBottomPadding.toInt())
}