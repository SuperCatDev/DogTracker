package com.sc.dtracker.features.map.ui

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.sc.dtracker.R
import com.sc.dtracker.features.location.domain.LocationController
import com.sc.dtracker.features.map.domain.mvi.MapFeature
import com.sc.dtracker.ui.views.bottomNavBarCornerHeight
import com.sc.dtracker.ui.views.bottomNavBarHeight
import org.koin.compose.getKoin

private const val MAP_VIEW_FROM_KEY = "MapComposeView"
private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun Context.asMapViewContainer() = (this as MapViewHost).provideMapViewContainer()

@Composable
fun MapComposeView(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val locationController: LocationController = getKoin().get()
    val mapFeature: MapFeature = getKoin().get()

    RequestMapPermissionsIfNeeded()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    locationController.requestStart(MAP_VIEW_FROM_KEY)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    locationController.requestStop(MAP_VIEW_FROM_KEY)
                }
                Lifecycle.Event.ON_CREATE -> Unit
                Lifecycle.Event.ON_START -> Unit
                Lifecycle.Event.ON_STOP -> Unit
                Lifecycle.Event.ON_DESTROY -> Unit
                Lifecycle.Event.ON_ANY -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            locationController.requestStop(MAP_VIEW_FROM_KEY)
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomNavBarHeight + 16.dp, start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        SquareFabButton(
            imagePainter = painterResource(R.drawable.button_jump_to_my_location),
            description = stringResource(id = R.string.icon_description_jump_to_location),
            size = 32.dp
        ) {
            mapFeature.onJumpToCurrentLocation()
        }
        Spacer(modifier = Modifier.height(12.dp))
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

@Composable
private fun RequestMapPermissionsIfNeeded() {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permsResults ->
        if (permsResults[locationPermissions[0]] != true || permsResults[locationPermissions[1]] != true) {
            showDialog = true
        }
    }

    if (showDialog) {
        NoMapPermissionView {
            showDialog = false
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(locationPermissions)
    }
}

@Composable
fun SquareFabButton(
    modifier: Modifier = Modifier,
    size: Dp,
    imagePainter: Painter,
    description: String,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            painter = imagePainter,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(size)
        )
    }
}