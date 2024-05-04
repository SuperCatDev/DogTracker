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
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.sc.dtracker.R
import com.sc.dtracker.ui.screens.MapScreen
import com.sc.dtracker.ui.screens.Settings
import com.sc.dtracker.ui.screens.StableTab
import com.sc.dtracker.ui.views.BottomNavBar
import com.sc.dtracker.ui.views.BottomNavBarItem
import kotlinx.collections.immutable.persistentListOf


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun BaseScreen(modifier: Modifier = Modifier) {

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

                var fabClicked by remember {
                    mutableStateOf(false)
                }

                BottomNavBar(
                    buttons = buttons,
                    fabImage = if (fabClicked) {
                        painterResource(R.drawable.record_button_stop)
                    } else {
                        painterResource(R.drawable.record_button_start)
                    },
                    fabOnClick = {
                        fabClicked = fabClicked.not()
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