package com.sc.dtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sc.dtracker.R
import com.sc.dtracker.features.location.ui.RecordLocationLauncher
import com.sc.dtracker.features.map.domain.mvi.MapFeature
import com.sc.dtracker.features.map.ui.NoMapPermissionView
import com.sc.dtracker.ui.screens.MapScreen
import com.sc.dtracker.ui.screens.Settings
import com.sc.dtracker.ui.screens.StableTab
import com.sc.dtracker.ui.views.BottomNavBar
import com.sc.dtracker.ui.views.BottomNavBarItem
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.getKoin

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun BaseScreen(modifier: Modifier = Modifier) {

    val recordLocationLauncher: RecordLocationLauncher = getKoin().get()

    TabNavigator(MapScreen) {
        Scaffold(
            content = {
                Box(
                    modifier = modifier
                ) {
                    CurrentTab()
                }
            },
            bottomBar = {
                val buttons = persistentListOf(
                    tabBottomBarItem(MapScreen),
                    tabBottomBarItem(Settings)
                )

                val context = LocalContext.current

                val locationRecordStarted = recordLocationLauncher.observeStarted().collectAsState()

                val locationPermissionsState =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                val location2PermissionsState =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION)

                var showDialog by remember {
                    mutableStateOf(false)
                }

                if (showDialog) {
                    NoMapPermissionView {
                        showDialog = false
                    }
                }

                BottomNavBar(
                    buttons = buttons,
                    fabImage = if (locationRecordStarted.value) {
                        painterResource(R.drawable.record_button_stop)
                    } else {
                        painterResource(R.drawable.record_button_start)
                    },
                    fabOnClick = {
                        if (locationRecordStarted.value) {
                            recordLocationLauncher.stop(context)
                        } else {
                            if (locationPermissionsState.status.isGranted && location2PermissionsState.status.isGranted) {
                                recordLocationLauncher.start(context)
                            } else {
                                showDialog = true
                            }
                        }
                    }
                )
            },
        )
    }
}

@Composable
private fun tabBottomBarItem(tab: StableTab): BottomNavBarItem {
    val tabNavigator = LocalTabNavigator.current

    return BottomNavBarItem(
        icon = tab.options.icon,
        text = tab.options.title,
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab }
    )
}