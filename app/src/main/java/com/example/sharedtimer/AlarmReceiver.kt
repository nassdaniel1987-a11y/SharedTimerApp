package com.example.sharedtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.example.sharedtimer.models.TimerData
import com.example.sharedtimer.utils.AlarmScheduler

class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "AlarmReceiver"
        private const val WAKELOCK_TIMEOUT = 5 * 60 * 1000L // 5 Minuten
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm empfangen!")
        
        if (intent.action != AlarmScheduler.ALARM_ACTION) {
            Log.w(TAG, "Falscher Intent Action: ${intent.action}")
            return
        }
        
        val timerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA, TimerData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA)
        }
        
        if (timerData == null) {
            Log.e(TAG, "Keine Timer-Daten im Intent!")
            return
        }
        
        Log.d(TAG, "Timer abgelaufen für: ${timerData.childName}")
        
        // KRITISCH: Wakelock halten, damit das Gerät nicht wieder einschläft
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "SharedTimer::AlarmWakeLock"
        )
        
        wakeLock.acquire(WAKELOCK_TIMEOUT)
        
        try {
            // Starte Fullscreen Activity
            val fullscreenIntent = Intent(context, AlarmFullscreenActivity::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_TIMER_DATA, timerData)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION
            }
            context.startActivity(fullscreenIntent)
            
            // Starte Alarm Sound Service
            val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_TIMER_DATA, timerData)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            
        } finally {
            // Wakelock nach kurzer Zeit wieder freigeben
            wakeLock.release()
        }
    }
}
