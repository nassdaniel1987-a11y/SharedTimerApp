package com.example.sharedtimer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedtimer.databinding.ItemTimerBinding
import com.example.sharedtimer.models.TimerData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class TimerAdapter(
    private val onDeleteClick: (TimerData) -> Unit
) : ListAdapter<TimerData, TimerAdapter.TimerViewHolder>(TimerDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = ItemTimerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimerViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TimerViewHolder(
        private val binding: ItemTimerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(timer: TimerData) {
            binding.apply {
                tvChildName.text = timer.childName
                
                // Formatiere Datum und Uhrzeit
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.GERMAN)
                
                tvDate.text = dateFormat.format(timer.targetTime)
                tvTime.text = "${timeFormat.format(timer.targetTime)} Uhr"
                
                // Berechne verbleibende Zeit
                val remainingMillis = timer.targetTime - System.currentTimeMillis()
                
                if (remainingMillis > 0) {
                    val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                    
                    tvRemainingTime.text = if (hours > 0) {
                        "In $hours Std. $minutes Min."
                    } else {
                        "In $minutes Min."
                    }
                    
                    tvRemainingTime.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    tvRemainingTime.text = "Abgelaufen"
                    tvRemainingTime.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_red_dark)
                    )
                }
                
                // Delete Button
                btnDelete.setOnClickListener {
                    onDeleteClick(timer)
                }
            }
        }
    }
    
    class TimerDiffCallback : DiffUtil.ItemCallback<TimerData>() {
        override fun areItemsTheSame(oldItem: TimerData, newItem: TimerData): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TimerData, newItem: TimerData): Boolean {
            return oldItem == newItem
        }
    }
}
