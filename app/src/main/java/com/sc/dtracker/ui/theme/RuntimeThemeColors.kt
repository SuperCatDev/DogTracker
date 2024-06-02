package com.sc.dtracker.ui.theme

import androidx.compose.ui.graphics.Color

val AlertButtonActionArea: Color
    get() {
        return if (isDarkThemeInCompose) {
            AlertButtonAreaDark
        } else {
            AlertButtonAreaLight
        }
    }

val AlertButtonActionText: Color
    get() {
        return if (isDarkThemeInCompose) {
            AlertButtonAreaTextOnDark
        } else {
            AlertButtonAreaTextOnLight
        }
    }