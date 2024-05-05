package com.sc.dtracker.features.location.ui

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sc.dtracker.R

class LocationNotificationControllerImpl : LocationNotificationController {

    override fun buildServiceNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getNotificationTitle(context))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    override fun registerNotificationChannel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val channel = NotificationChannelCompat.Builder(
                    NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW,
                )
                    .setName(getNotificationChannelName(context))
                    .build()

                val manager = NotificationManagerCompat.from(context)
                manager.createNotificationChannel(channel)
            }
        } catch (th: Throwable) {
            Log.e("NotificationChannel", "Error: ${th.stackTraceToString()}")
        }
    }

    private fun getNotificationChannelName(context: Context): String {
        return context.getString(R.string.location_channel_name)
    }

    private fun getNotificationTitle(context: Context): String {
        return context.getString(R.string.location_notification_title)
    }

    private companion object {
        const val NOTIFICATION_CHANNEL_ID = "location"
    }
}