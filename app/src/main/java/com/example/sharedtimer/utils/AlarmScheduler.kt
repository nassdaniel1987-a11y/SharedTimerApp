package com.example.sharedtimer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.sharedtimer.AlarmReceiver
import com.example.sharedtimer.models.TimerData

class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    companion object {
        private const val TAG = "AlarmScheduler"
        const val EXTRA_TIMER_DATA = "extra_timer_data"
        const val ALARM_ACTION = "com.example.sharedtimer.ALARM_ACTION"
    }
    
    /**
     * Setzt einen präzisen Alarm, der auch im Doze Mode feuert
     */
    fun scheduleAlarm(timerData: TimerData) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
            putExtra(EXTRA_TIMER_DATA, timerData)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timerData.id.hashCode(), // Eindeutige Request ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = timerData.targetTime
        
        // Prüfe ob die Zeit in der Zukunft liegt
        if (triggerTime <= System.currentTimeMillis()) {
            Log.w(TAG, "Timer-Zeit liegt in der Vergangenheit! Timer wird sofort ausgelöst.")
            // Optional: Alarm sofort auslösen
            context.sendBroadcast(intent)
            return
        }
        
        try {
            // KRITISCH: setExactAndAllowWhileIdle für Doze Mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d(TAG, "Alarm gesetzt mit setExactAndAllowWhileIdle für ${timerData.childName} um ${java.util.Date(triggerTime)}")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d(TAG, "Alarm gesetzt mit setExact für ${timerData.childName}")
            }
            
            // Speichere Timer-ID für spätere Referenz
            saveScheduledAlarm(timerData.id, triggerTime)
            
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException beim Setzen des Alarms. SCHEDULE_EXACT_ALARM fehlt!", e)
        }
    }
    
    /**
     * Löscht einen Alarm
     */
    fun cancelAlarm(timerId: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timerId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm abgebrochen für Timer: $timerId")
        }
        
        removeScheduledAlarm(timerId)
    }
    
    /**
     * Prüft ob EXACT_ALARM Permission vorhanden ist (Android 12+)
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
    
    // Hilfs-Methoden zum Speichern aktiver Alarme
    private fun saveScheduledAlarm(timerId: String, triggerTime: Long) {
        val prefs = context.getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)
        prefs.edit().putLong(timerId, triggerTime).apply()
    }
    
    private fun removeScheduledAlarm(timerId: String) {
        val prefs = context.getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)
        prefs.edit().remove(timerId).apply()
    }
    
    /**
     * Lädt alle geplanten Alarme (z.B. nach Boot)
     */
    fun getScheduledAlarms(): Map<String, Long> {
        val prefs = context.getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)
        return prefs.all.mapValues { it.value as Long }
    }
}
