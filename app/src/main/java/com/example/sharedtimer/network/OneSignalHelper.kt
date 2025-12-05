package com.example.sharedtimer.network

import android.content.Context
import android.util.Log
import com.example.sharedtimer.models.TimerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OneSignalHelper(private val context: Context) {
    
    private val apiService = OneSignalApiService.create()
    
    companion object {
        private const val TAG = "OneSignalHelper"
        // WICHTIG: Ersetze mit deiner OneSignal App ID
        private const val ONESIGNAL_APP_ID = "YOUR_ONESIGNAL_APP_ID"
    }
    
    /**
     * Sendet eine Push-Notification an alle Geräte, um Timer zu synchronisieren
     */
    suspend fun notifyNewTimer(timerData: TimerData): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = OneSignalNotificationRequest(
                appId = ONESIGNAL_APP_ID,
                includedSegments = listOf("All"),
                headings = mapOf("en" to "Neuer Abhol-Timer"),
                contents = mapOf("en" to "Timer für ${timerData.childName} wurde erstellt"),
                data = mapOf(
                    "type" to "new_timer",
                    "timer_id" to timerData.id,
                    "child_name" to timerData.childName,
                    "target_time" to timerData.targetTime,
                    "created_by" to timerData.createdBy
                ),
                priority = 10,
                contentAvailable = true
            )
            
            Log.d(TAG, "Sende OneSignal Notification für Timer: ${timerData.id}")
            
            val response = apiService.sendNotification(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Notification erfolgreich gesendet. Recipients: ${body?.recipients}")
                Result.success("Benachrichtigung an ${body?.recipients} Geräte gesendet")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Fehler beim Senden: ${response.code()} - $errorBody")
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception beim Senden der Notification", e)
            Result.failure(e)
        }
    }
    
    /**
     * Benachrichtigt alle Geräte, dass ein Timer abgebrochen wurde
     */
    suspend fun notifyCancelTimer(timerId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = OneSignalNotificationRequest(
                appId = ONESIGNAL_APP_ID,
                includedSegments = listOf("All"),
                headings = mapOf("en" to "Timer abgebrochen"),
                contents = mapOf("en" to "Ein Timer wurde abgebrochen"),
                data = mapOf(
                    "type" to "cancel_timer",
                    "timer_id" to timerId
                ),
                priority = 10,
                contentAvailable = true
            )
            
            val response = apiService.sendNotification(request)
            
            if (response.isSuccessful) {
                Log.d(TAG, "Cancel-Notification erfolgreich gesendet")
                Result.success("Timer-Abbruch gesendet")
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception beim Senden der Cancel-Notification", e)
            Result.failure(e)
        }
    }
}
