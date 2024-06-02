package com.sc.dtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sc.dtracker.features.map.ui.MapViewContainer
import com.sc.dtracker.features.map.ui.MapViewHost
import com.sc.dtracker.ui.BaseScreen
import com.sc.dtracker.ui.ext.lazyUnsafe
import com.sc.dtracker.ui.theme.DogTrackerTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity(), MapViewHost {

    private val mapViewContainer by lazyUnsafe {
        MapViewContainer(this)
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

    private companion object {
        const val PERMISSION_REQUEST_CODE = 2142
    }
}