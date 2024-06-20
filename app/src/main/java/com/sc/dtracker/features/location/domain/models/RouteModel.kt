package com.sc.dtracker.features.location.domain.models

data class RouteModel(
    val id: Int,
    val color: Int,
    val name: String,
    val points: List<Location>
)