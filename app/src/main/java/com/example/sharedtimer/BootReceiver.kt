package com.example.sharedtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.sharedtimer.repository.TimerRepository
import com.example.sharedtimer.utils.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON") {
            return
        }
        
        Log.d(TAG, "Boot completed - Timer werden wiederhergestellt")
        
        val pendingResult = goAsync()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                restoreTimers(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    private suspend fun restoreTimers(context: Context) {
        val repository = TimerRepository()
        val scheduler = AlarmScheduler(context)
        
        // Lade alle aktiven Timer aus Firestore
        val result = repository.getAllTimers()
        
        if (result.isSuccess) {
            val timers = result.getOrNull() ?: emptyList()
            
            Log.d(TAG, "Stelle ${timers.size} Timer wieder her")
            
            timers.forEach { timer ->
                // Nur Timer in der Zukunft wiederherstellen
                if (timer.targetTime > System.currentTimeMillis() && timer.isActive) {
                    scheduler.scheduleAlarm(timer)
                    Log.d(TAG, "Timer wiederhergestellt: ${timer.childName}")
                }
            }
        } else {
            Log.e(TAG, "Fehler beim Wiederherstellen der Timer", result.exceptionOrNull())
        }
    }
}
