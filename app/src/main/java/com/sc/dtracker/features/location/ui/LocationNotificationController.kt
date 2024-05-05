package com.sc.dtracker.features.location.ui

import android.app.Notification
import android.content.Context

interface LocationNotificationController {

    fun buildServiceNotification(context: Context): Notification
    fun registerNotificationChannel(context: Context)
}