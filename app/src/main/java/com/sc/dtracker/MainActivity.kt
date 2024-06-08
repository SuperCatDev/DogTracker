package com.sc.dtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.coroutineScope
import com.sc.dtracker.features.map.domain.MapRestoreStateHolder
import com.sc.dtracker.features.map.domain.mvi.MapFeature
import com.sc.dtracker.features.map.ui.MapViewContainer
import com.sc.dtracker.features.map.ui.MapViewHost
import com.sc.dtracker.ui.BaseScreen
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.sc.dtracker.ui.theme.DogTrackerTheme
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity(), MapViewHost {

    private val stateHolder: MapRestoreStateHolder by inject()
    private val coroutineScope = lifecycle.coroutineScope
    private val mapFeature: MapFeature by inject()

    private val mapViewContainer by lazyUnsafe {
        MapViewContainer(
            this,
            mapFeature,
            coroutineScope,
            stateHolder
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        enableEdgeToEdge()

        setContent {
            DogTrackerTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BaseScreen()
                }
            }
        }
        mapViewContainer.onCreate()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapViewContainer.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mapViewContainer.onStop()
        super.onStop()
    }

    override fun provideMapViewContainer(): MapViewContainer {
        return mapViewContainer
    }
}