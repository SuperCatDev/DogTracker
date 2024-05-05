package com.sc.dtracker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.sc.dtracker.R
import com.sc.dtracker.features.location.ui.LocationLauncher
import com.sc.dtracker.ui.screens.MapScreen
import com.sc.dtracker.ui.screens.Settings
import com.sc.dtracker.ui.screens.StableTab
import com.sc.dtracker.ui.views.BottomNavBar
import com.sc.dtracker.ui.views.BottomNavBarItem
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.getKoin


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun BaseScreen(modifier: Modifier = Modifier) {

    val locationLauncher: LocationLauncher = getKoin().get()

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

                var locationRecordStarted by remember {
                    mutableStateOf(locationLauncher.isStarted(context))
                }

                BottomNavBar(
                    buttons = buttons,
                    fabImage = if (locationRecordStarted) {
                        painterResource(R.drawable.record_button_stop)
                    } else {
                        painterResource(R.drawable.record_button_start)
                    },
                    fabOnClick = {
                        if (locationRecordStarted) {
                            locationLauncher.stop(context)
                        } else {
                            locationLauncher.start(context)
                        }

                        locationRecordStarted = locationRecordStarted.not()
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