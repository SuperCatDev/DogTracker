package com.sc.dtracker.features.map.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.yandex.mapkit.mapview.MapView

private var cachedMapView: MapView? = null

fun getMapView(context: Context): MapView {
    return cachedMapView ?: MapView(context).also {
        cachedMapView = it
    }
}

@Composable
fun MapComposeView() {
/*
    // Safely update the current lambdas when a new one is provided
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    Log.e("VVV", "Local ctx: ${LocalContext.current}")

    var callOnStart by remember {
        mutableStateOf({})
    }

    var callOnStop by remember {
        mutableStateOf({})
    }

    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for lifecycle events
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    callOnStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    callOnStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.e("VVV", "ON DESTROY!")
                    cachedMapView = null
                }
                else -> {}
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }*/

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            (context as MapViewHost).provideMapViewContainer().getView()
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication

        },
    )
}