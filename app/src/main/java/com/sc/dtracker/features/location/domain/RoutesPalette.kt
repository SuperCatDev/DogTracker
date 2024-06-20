package com.sc.dtracker.features.location.domain

interface RoutesPalette {

    fun getColorFromPaletteFor(routeId: Int): Int
}