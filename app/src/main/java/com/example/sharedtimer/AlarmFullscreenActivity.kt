package com.example.sharedtimer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedtimer.databinding.ActivityAlarmFullscreenBinding
import com.example.sharedtimer.models.TimerData
import com.example.sharedtimer.utils.AlarmScheduler

class AlarmFullscreenActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAlarmFullscreenBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // KRITISCH: Activity über Lockscreen anzeigen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        
        // Keyguard deaktivieren (optional, aber hilfreich)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
        
        binding = ActivityAlarmFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val timerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA, TimerData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA)
        }
        
        if (timerData == null) {
            finish()
            return
        }
        
        setupUI(timerData)
    }
    
    private fun setupUI(timerData: TimerData) {
        binding.apply {
            tvChildName.text = timerData.childName
            tvMessage.text = "Es ist Zeit, ${timerData.childName} abzuholen!"
            
            btnStopAlarm.setOnClickListener {
                stopAlarm()
                finish()
            }
            
            btnSnooze.setOnClickListener {
                // Optional: 5 Minuten snoozen
                snoozeAlarm(timerData)
                finish()
            }
        }
    }
    
    private fun stopAlarm() {
        // Sound Service stoppen
        val stopIntent = Intent(this, AlarmSoundService::class.java).apply {
            action = AlarmSoundService.ACTION_STOP_ALARM
        }
        startService(stopIntent)
    }
    
    private fun snoozeAlarm(timerData: TimerData) {
        stopAlarm()
        
        // Neuen Timer für 5 Minuten später setzen
        val snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000)
        val snoozedTimer = timerData.copy(
            targetTime = snoozeTime
        )
        
        val scheduler = AlarmScheduler(this)
        scheduler.scheduleAlarm(snoozedTimer)
    }
}
