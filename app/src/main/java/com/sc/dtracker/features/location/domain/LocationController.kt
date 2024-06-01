package com.sc.dtracker.features.location.domain

import androidx.annotation.MainThread

interface LocationController {

    @MainThread
    fun requestStart(from: String)
    @MainThread
    fun requestStop(from: String)
}