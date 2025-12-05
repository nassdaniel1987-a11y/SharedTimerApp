package com.example.sharedtimer.network

import com.google.gson.annotations.SerializedName

data class OneSignalNotificationRequest(
    @SerializedName("app_id")
    val appId: String,
    
    @SerializedName("included_segments")
    val includedSegments: List<String> = listOf("All"), // Alle User
    
    @SerializedName("contents")
    val contents: Map<String, String>,
    
    @SerializedName("headings")
    val headings: Map<String, String>,
    
    @SerializedName("data")
    val data: Map<String, Any>, // Custom Data für Timer-Sync
    
    @SerializedName("priority")
    val priority: Int = 10, // Höchste Priorität
    
    @SerializedName("content_available")
    val contentAvailable: Boolean = true, // Silent Push für iOS
    
    @SerializedName("android_channel_id")
    val androidChannelId: String = "alarm_channel",
    
    @SerializedName("android_visibility")
    val androidVisibility: Int = 1, // Public (auch auf Lockscreen)
    
    @SerializedName("ttl")
    val ttl: Int = 600 // Time to live: 10 Minuten
)

data class OneSignalNotificationResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("recipients")
    val recipients: Int,
    
    @SerializedName("errors")
    val errors: List<String>? = null
)
