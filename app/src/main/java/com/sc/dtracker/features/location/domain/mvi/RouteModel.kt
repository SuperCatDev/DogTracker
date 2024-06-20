package com.sc.dtracker.features.location.domain.mvi

import com.sc.dtracker.features.location.domain.models.Location

data class RouteModel(
    val id: Int,
    val color: Int,
    val name: String,
    val points: List<Location>
)