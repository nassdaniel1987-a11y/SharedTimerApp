package com.example.sharedtimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sharedtimer.models.TimerData
import com.example.sharedtimer.utils.AlarmScheduler

class AlarmSoundService : Service() {
    
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    
    companion object {
        private const val TAG = "AlarmSoundService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "alarm_sound_channel"
        private const val WAKELOCK_TAG = "SharedTimer::SoundServiceWakeLock"
        
        const val ACTION_STOP_ALARM = "com.example.sharedtimer.STOP_ALARM"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Wakelock halten während Sound läuft
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKELOCK_TAG
        )
        wakeLock?.acquire(10 * 60 * 1000L) // Max 10 Minuten
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarmSound()
            stopSelf()
            return START_NOT_STICKY
        }
        
        val timerData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA, TimerData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(AlarmScheduler.EXTRA_TIMER_DATA)
        }
        
        if (timerData == null) {
            Log.e(TAG, "Keine Timer-Daten!")
            stopSelf()
            return START_NOT_STICKY
        }
        
        // Foreground Notification
        val notification = createNotification(timerData)
        startForeground(NOTIFICATION_ID, notification)
        
        // Sound und Vibration starten
        startAlarmSound()
        startVibration()
        
        return START_STICKY
    }
    
    private fun startAlarmSound() {
        try {
            // KRITISCH: AudioAttributes für ALARM setzen!
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                
                // Lege deine Alarm-Sound-Datei in res/raw/alarm_sound.mp3
                setDataSource(applicationContext, android.net.Uri.parse(
                    "android.resource://${packageName}/${R.raw.alarm_sound}"
                ))
                
                isLooping = true
                
                // Maximale Lautstärke
                setVolume(1.0f, 1.0f)
                
                prepare()
                start()
                
                Log.d(TAG, "Alarm-Sound gestartet")
            }
            
            // Audio Focus anfordern (damit andere Apps pausieren)
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(
                    AudioManager.AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(audioAttributes)
                        .build()
                )
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Abspielen des Sounds", e)
        }
    }
    
    private fun startVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 1000, 500, 1000, 500)
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, 0) // 0 = Loop
            )
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 1000, 500, 1000, 500)
            vibrator?.vibrate(pattern, 0)
        }
    }
    
    private fun stopAlarmSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        
        vibrator?.cancel()
        vibrator = null
        
        wakeLock?.release()
        wakeLock = null
        
        Log.d(TAG, "Alarm-Sound gestoppt")
    }
    
    private fun createNotification(timerData: TimerData): Notification {
        val stopIntent = Intent(this, AlarmSoundService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Abhol-Timer")
            .setContentText("Zeit für ${timerData.childName}!")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_stop,
                "Stoppen",
                stopPendingIntent
            )
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Sound",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Spielt Alarm-Sound ab"
                setSound(null, null) // Kein Sound via Notification
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}
