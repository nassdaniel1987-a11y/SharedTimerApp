package com.example.sharedtimer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sharedtimer.models.TimerData
import com.example.sharedtimer.network.OneSignalHelper
import com.example.sharedtimer.repository.TimerRepository
import com.example.sharedtimer.utils.AlarmScheduler
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = TimerRepository()
    private val oneSignalHelper = OneSignalHelper(application)
    private val alarmScheduler = AlarmScheduler(application)
    
    private val _timers = MutableLiveData<List<TimerData>>()
    val timers: LiveData<List<TimerData>> = _timers
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage
    
    init {
        observeTimers()
    }
    
    /**
     * Lauscht auf Firestore-Änderungen in Echtzeit
     */
    private fun observeTimers() {
        viewModelScope.launch {
            repository.getActiveTimersFlow().collect { timerList ->
                _timers.postValue(timerList)
                
                // Lokale Alarme für alle Timer setzen
                timerList.forEach { timer ->
                    if (timer.targetTime > System.currentTimeMillis()) {
                        alarmScheduler.scheduleAlarm(timer)
                    }
                }
            }
        }
    }
    
    /**
     * Erstellt einen neuen Timer
     */
    fun createTimer(childName: String, targetTimeMillis: Long, createdBy: String) {
        if (childName.isBlank()) {
            _errorMessage.value = "Bitte gib einen Namen ein"
            return
        }
        
        if (targetTimeMillis <= System.currentTimeMillis()) {
            _errorMessage.value = "Die Zeit muss in der Zukunft liegen"
            return
        }
        
        _isLoading.value = true
        
        val timerData = TimerData(
            id = UUID.randomUUID().toString(),
            childName = childName,
            targetTime = targetTimeMillis,
            createdBy = createdBy,
            createdAt = System.currentTimeMillis(),
            isActive = true
        )
        
        viewModelScope.launch {
            // 1. In Firestore speichern
            val firestoreResult = repository.createTimer(timerData)
            
            if (firestoreResult.isSuccess) {
                // 2. Lokalen Alarm setzen
                alarmScheduler.scheduleAlarm(timerData)
                
                // 3. OneSignal Notification senden
                val oneSignalResult = oneSignalHelper.notifyNewTimer(timerData)
                
                if (oneSignalResult.isSuccess) {
                    _successMessage.postValue("Timer erstellt und an alle Geräte gesendet!")
                } else {
                    _successMessage.postValue("Timer erstellt (Benachrichtigung fehlgeschlagen)")
                }
            } else {
                _errorMessage.postValue("Fehler beim Erstellen: ${firestoreResult.exceptionOrNull()?.message}")
            }
            
            _isLoading.postValue(false)
        }
    }
    
    /**
     * Löscht einen Timer
     */
    fun deleteTimer(timerId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            // 1. Lokalen Alarm abbrechen
            alarmScheduler.cancelAlarm(timerId)
            
            // 2. In Firestore löschen
            val result = repository.deleteTimer(timerId)
            
            if (result.isSuccess) {
                // 3. Andere Geräte benachrichtigen
                oneSignalHelper.notifyCancelTimer(timerId)
                _successMessage.postValue("Timer gelöscht")
            } else {
                _errorMessage.postValue("Fehler beim Löschen: ${result.exceptionOrNull()?.message}")
            }
            
            _isLoading.postValue(false)
        }
    }
    
    /**
     * Lädt alle Timer neu
     */
    fun refreshTimers() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            
            val result = repository.getAllTimers()
            
            if (result.isSuccess) {
                _timers.postValue(result.getOrNull() ?: emptyList())
            } else {
                _errorMessage.postValue("Fehler beim Laden: ${result.exceptionOrNull()?.message}")
            }
            
            _isLoading.postValue(false)
        }
    }
    
    /**
     * Prüft ob Exact Alarm Permission vorhanden ist
     */
    fun checkAlarmPermission(): Boolean {
        return alarmScheduler.canScheduleExactAlarms()
    }
}
