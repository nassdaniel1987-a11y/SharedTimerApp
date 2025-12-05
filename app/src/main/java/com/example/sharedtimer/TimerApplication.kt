package com.example.sharedtimer

import android.app.Application
import com.example.sharedtimer.utils.NotificationHelper
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

class TimerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Firebase initialisieren
        FirebaseApp.initializeApp(this)
        
        // Notification Channels erstellen
        NotificationHelper.createNotificationChannels(this)
        
        // OneSignal initialisieren
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        
        // WICHTIG: Ersetze mit deiner OneSignal App ID
        OneSignal.initWithContext(this, "YOUR_ONESIGNAL_APP_ID")
        
        // Optional: User Consent einholen (DSGVO)
        OneSignal.Notifications.requestPermission(true)
        
        // Optional: OneSignal User ID loggen
        OneSignal.User.pushSubscription.id?.let { userId ->
            android.util.Log.d("TimerApplication", "OneSignal User ID: $userId")
        }
    }
}
