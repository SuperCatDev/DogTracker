package com.sc.dtracker.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.sc.dtracker.ui.screens.MapScreen
import com.sc.dtracker.ui.screens.Settings
import com.sc.dtracker.ui.screens.StableTab
import com.sc.dtracker.ui.views.BottomNavBar
import com.sc.dtracker.ui.views.BottomNavBarItem
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BaseScreen(modifier: Modifier = Modifier) {

    TabNavigator(MapScreen) {
        Scaffold(
            content = {
                Box(modifier = modifier.consumeWindowInsets(it)) {
                    CurrentTab()
                }
            },
            bottomBar = {
                val context = LocalContext.current
                val buttons = persistentListOf(
                    tabBottomBarItem(MapScreen),
                    tabBottomBarItem(Settings)
                )

                BottomNavBar(buttons = buttons, fabOnClick = {
                    Toast.makeText(context, "FAB clicked", Toast.LENGTH_SHORT).show()
                })
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