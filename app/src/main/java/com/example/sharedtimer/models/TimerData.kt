package com.example.sharedtimer.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimerData(
    val id: String = "",
    val childName: String = "",
    val targetTime: Long = 0L, // Milliseconds seit Epoch
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) : Parcelable {
    
    // Für Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "childName" to childName,
            "targetTime" to targetTime,
            "createdBy" to createdBy,
            "createdAt" to createdAt,
            "isActive" to isActive
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): TimerData {
            return TimerData(
                id = map["id"] as? String ?: "",
                childName = map["childName"] as? String ?: "",
                targetTime = (map["targetTime"] as? Long) ?: 0L,
                createdBy = map["createdBy"] as? String ?: "",
                createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis(),
                isActive = (map["isActive"] as? Boolean) ?: true
            )
        }
    }
}
