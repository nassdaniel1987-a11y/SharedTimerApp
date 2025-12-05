package com.example.sharedtimer.repository

import android.util.Log
import com.example.sharedtimer.models.TimerData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TimerRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val timersCollection = firestore.collection("timers")
    
    companion object {
        private const val TAG = "TimerRepository"
    }
    
    /**
     * Erstellt einen neuen Timer in Firestore
     */
    suspend fun createTimer(timerData: TimerData): Result<String> {
        return try {
            val documentRef = timersCollection.document(timerData.id)
            documentRef.set(timerData.toMap()).await()
            
            Log.d(TAG, "Timer erfolgreich erstellt: ${timerData.id}")
            Result.success(timerData.id)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Erstellen des Timers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Aktualisiert einen Timer
     */
    suspend fun updateTimer(timerData: TimerData): Result<Unit> {
        return try {
            timersCollection.document(timerData.id)
                .set(timerData.toMap())
                .await()
            
            Log.d(TAG, "Timer aktualisiert: ${timerData.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Aktualisieren des Timers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Löscht einen Timer (setzt isActive auf false)
     */
    suspend fun deleteTimer(timerId: String): Result<Unit> {
        return try {
            timersCollection.document(timerId)
                .update("isActive", false)
                .await()
            
            Log.d(TAG, "Timer gelöscht: $timerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Löschen des Timers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Lädt einen einzelnen Timer
     */
    suspend fun getTimer(timerId: String): Result<TimerData?> {
        return try {
            val document = timersCollection.document(timerId).get().await()
            
            if (document.exists()) {
                val data = document.data ?: return Result.success(null)
                val timer = TimerData.fromMap(data)
                Result.success(timer)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden des Timers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Lädt alle aktiven Timer als Flow (Echtzeit-Updates!)
     */
    fun getActiveTimersFlow(): Flow<List<TimerData>> = callbackFlow {
        val listenerRegistration = timersCollection
            .whereEqualTo("isActive", true)
            .orderBy("targetTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Fehler beim Lauschen auf Timer-Updates", error)
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val timers = snapshot.documents.mapNotNull { doc ->
                        try {
                            TimerData.fromMap(doc.data ?: return@mapNotNull null)
                        } catch (e: Exception) {
                            Log.e(TAG, "Fehler beim Parsen von Timer: ${doc.id}", e)
                            null
                        }
                    }
                    
                    Log.d(TAG, "Timer-Update empfangen: ${timers.size} aktive Timer")
                    trySend(timers)
                }
            }
        
        awaitClose {
            listenerRegistration.remove()
            Log.d(TAG, "Firestore Listener geschlossen")
        }
    }
    
    /**
     * Lädt alle Timer (einmalig)
     */
    suspend fun getAllTimers(): Result<List<TimerData>> {
        return try {
            val snapshot = timersCollection
                .whereEqualTo("isActive", true)
                .orderBy("targetTime", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val timers = snapshot.documents.mapNotNull { doc ->
                try {
                    TimerData.fromMap(doc.data ?: return@mapNotNull null)
                } catch (e: Exception) {
                    Log.e(TAG, "Fehler beim Parsen von Timer: ${doc.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "${timers.size} Timer geladen")
            Result.success(timers)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Laden aller Timer", e)
            Result.failure(e)
        }
    }
}
