package com.example.sharedtimer

import android.content.Context
import android.util.Log
import com.example.sharedtimer.models.TimerData
import com.example.sharedtimer.utils.AlarmScheduler
import com.onesignal.notifications.IActionButton
import com.onesignal.notifications.IDisplayableMutableNotification
import com.onesignal.notifications.INotificationReceivedEvent
import com.onesignal.notifications.INotificationServiceExtension

class OneSignalNotificationService : INotificationServiceExtension {
    
    companion object {
        private const val TAG = "OneSignalNotifService"
    }
    
    override fun onNotificationReceived(event: INotificationReceivedEvent) {
        val notification = event.notification
        val data = notification.additionalData
        
        Log.d(TAG, "OneSignal Notification empfangen: ${notification.body}")
        Log.d(TAG, "Data: $data")
        
        if (data != null) {
            val type = data.optString("type", "")
            
            when (type) {
                "new_timer" -> {
                    handleNewTimer(event.context, data)
                }
                "cancel_timer" -> {
                    handleCancelTimer(event.context, data)
                }
                else -> {
                    Log.w(TAG, "Unbekannter Notification-Typ: $type")
                }
            }
        }
        
        // Zeige die Notification an (oder unterdrücke sie)
        // event.preventDefault() würde die Notification unterdrücken
    }
    
    private fun handleNewTimer(context: Context, data: org.json.JSONObject) {
        try {
            val timerId = data.getString("timer_id")
            val childName = data.getString("child_name")
            val targetTime = data.getLong("target_time")
            val createdBy = data.getString("created_by")
            
            val timerData = TimerData(
                id = timerId,
                childName = childName,
                targetTime = targetTime,
                createdBy = createdBy,
                isActive = true
            )
            
            // Lokalen Alarm setzen
            val scheduler = AlarmScheduler(context)
            scheduler.scheduleAlarm(timerData)
            
            Log.d(TAG, "Lokaler Alarm gesetzt für: $childName")
            
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Verarbeiten des neuen Timers", e)
        }
    }
    
    private fun handleCancelTimer(context: Context, data: org.json.JSONObject) {
        try {
            val timerId = data.getString("timer_id")
            
            // Lokalen Alarm abbrechen
            val scheduler = AlarmScheduler(context)
            scheduler.cancelAlarm(timerId)
            
            Log.d(TAG, "Lokaler Alarm abgebrochen für Timer: $timerId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Abbrechen des Timers", e)
        }
    }
}
