package com.sc.dtracker.features.map.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.sc.dtracker.features.location.domain.LocationChannelOutput
import com.sc.dtracker.features.location.domain.models.LocationState
import com.sc.dtracker.features.location.domain.models.MyLocation
import com.sc.dtracker.features.map.data.MapStartLocationRepository
import com.sc.dtracker.ui.views.bottomNavBarHeight
import org.koin.compose.getKoin

private fun Context.asMapViewContainer() = (this as MapViewHost).provideMapViewContainer()

@Composable
fun MapComposeView() {

    val locationOutput: LocationChannelOutput = getKoin().get()
    val mapStartLocationRepository: MapStartLocationRepository = getKoin().get()

    val locationState = locationOutput.observeLocationState()
        .collectAsState(initial = LocationState.NoActive)

    val initialLocationState = remember {
        mutableStateOf<MyLocation?>(null)
    }

    LaunchedEffect(key1 = Unit) {
        initialLocationState.value = mapStartLocationRepository.getAndConsume()
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomNavBarHeight),
        factory = { context ->
            context.asMapViewContainer().getView()
        },
        update = { view ->
            when (val value = locationState.value) {
                is LocationState.Error -> {}
                is LocationState.Value -> {
                    view.context.asMapViewContainer().moveToLocation(value.location)
                }
                is LocationState.NoActive -> {
                    initialLocationState.value?.let {
                        view.context.asMapViewContainer().moveToLocation(it)
                    }
                }
            }
        },
    )
}