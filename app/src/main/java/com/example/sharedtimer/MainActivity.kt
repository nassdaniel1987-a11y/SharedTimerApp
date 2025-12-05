package com.example.sharedtimer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedtimer.adapters.TimerAdapter
import com.example.sharedtimer.databinding.ActivityMainBinding
import com.example.sharedtimer.viewmodel.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var timerAdapter: TimerAdapter
    
    private var selectedTimeMillis: Long = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        checkPermissions()
    }
    
    private fun setupRecyclerView() {
        timerAdapter = TimerAdapter(
            onDeleteClick = { timer ->
                showDeleteConfirmation(timer.id)
            }
        )
        
        binding.rvTimers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = timerAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSelectDateTime.setOnClickListener {
            showDateTimePicker()
        }
        
        binding.btnCreateTimer.setOnClickListener {
            createTimer()
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshTimers()
        }
    }
    
    private fun observeViewModel() {
        viewModel.timers.observe(this) { timers ->
            timerAdapter.submitList(timers)
            binding.swipeRefresh.isRefreshing = false
            
            if (timers.isEmpty()) {
                binding.tvEmptyState.visibility = android.view.View.VISIBLE
                binding.rvTimers.visibility = android.view.View.GONE
            } else {
                binding.tvEmptyState.visibility = android.view.View.GONE
                binding.rvTimers.visibility = android.view.View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
        
        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showDateTimePicker() {
        // Zuerst Datum auswählen
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Abhol-Datum auswählen")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        
        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            // Dann Uhrzeit auswählen
            showTimePicker(selectedDate)
        }
        
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
    
    private fun showTimePicker(selectedDate: Long) {
        val calendar = Calendar.getInstance()
        
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Abhol-Uhrzeit auswählen")
            .build()
        
        timePicker.addOnPositiveButtonClickListener {
            // Kombiniere Datum und Uhrzeit
            val dateCalendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            selectedTimeMillis = dateCalendar.timeInMillis
            
            // Zeige ausgewählte Zeit an
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN)
            binding.tvSelectedTime.text = "Ausgewählt: ${dateFormat.format(selectedTimeMillis)}"
            binding.tvSelectedTime.visibility = android.view.View.VISIBLE
        }
        
        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }
    
    private fun createTimer() {
        val childName = binding.etChildName.text.toString().trim()
        
        if (selectedTimeMillis == 0L) {
            Toast.makeText(this, "Bitte wähle Datum und Uhrzeit", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (childName.isEmpty()) {
            binding.etChildName.error = "Bitte Namen eingeben"
            return
        }
        
        // Erstelle Timer
        viewModel.createTimer(
            childName = childName,
            targetTimeMillis = selectedTimeMillis,
            createdBy = "Lehrer_${System.currentTimeMillis() % 1000}" // Platzhalter
        )
        
        // Zurücksetzen
        binding.etChildName.text?.clear()
        binding.tvSelectedTime.visibility = android.view.View.GONE
        selectedTimeMillis = 0L
    }
    
    private fun showDeleteConfirmation(timerId: String) {
        AlertDialog.Builder(this)
            .setTitle("Timer löschen?")
            .setMessage("Möchtest du diesen Timer wirklich löschen?")
            .setPositiveButton("Löschen") { _, _ ->
                viewModel.deleteTimer(timerId)
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }
    
    private fun checkPermissions() {
        // 1. Exact Alarm Permission (Android 12+)
        if (!viewModel.checkAlarmPermission()) {
            showAlarmPermissionDialog()
        }
        
        // 2. Battery Optimization ausschalten
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as android.os.PowerManager
            
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                showBatteryOptimizationDialog()
            }
        }
        
        // 3. Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) 
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
    
    private fun showAlarmPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alarm-Berechtigung erforderlich")
            .setMessage("Diese App benötigt die Berechtigung für exakte Alarme, um Timer zuverlässig auszulösen.")
            .setPositiveButton("Einstellungen öffnen") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
            .setNegativeButton("Später", null)
            .show()
    }
    
    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Akku-Optimierung deaktivieren")
            .setMessage("Bitte deaktiviere die Akku-Optimierung für diese App, damit Timer auch im Standby funktionieren.")
            .setPositiveButton("Einstellungen öffnen") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                }
            }
            .setNegativeButton("Später", null)
            .show()
    }
}
