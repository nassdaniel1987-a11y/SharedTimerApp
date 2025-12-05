package com.example.sharedtimer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.example.sharedtimer.R

object NotificationHelper {
    
    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val TIMER_CHANNEL_ID = "timer_channel"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Alarm Channel (Höchste Priorität)
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm Benachrichtigungen",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Zeigt Alarm-Benachrichtigungen an, wenn Timer ablaufen"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            
            // Timer Channel (Mittlere Priorität)
            val timerChannel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "Timer Benachrichtigungen",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Zeigt Informationen zu aktiven Timern"
            }
            
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(timerChannel)
        }
    }
}
