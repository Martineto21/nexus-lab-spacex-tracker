package com.nexuslab.spacextracker.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CountdownState(
    val nextLaunch: Launch? = null,
    val timeRemaining: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

data class TimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)

class CountdownViewModel @Inject constructor(
    private val repository: SpaceXRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CountdownState())
    val state: StateFlow<CountdownState> = _state
    
    init {
        loadNextLaunch()
        startCountdown()
    }
    
    private fun loadNextLaunch() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val launches = repository.getAllLaunches()
                
                val nextLaunch = launches
                    .filter { it.upcoming }
                    .sortedBy { it.dateUtc }
                    .firstOrNull()
                
                _state.value = _state.value.copy(
                    nextLaunch = nextLaunch,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar el próximo lanzamiento: ${e.message}"
                )
            }
        }
    }
    
    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                val nextLaunch = _state.value.nextLaunch
                if (nextLaunch != null) {
                    val timeRemaining = calculateTimeRemaining(nextLaunch.dateUtc)
                    _state.value = _state.value.copy(
                        timeRemaining = formatTimeRemaining(timeRemaining)
                    )
                }
                delay(1000) // Actualizar cada segundo
            }
        }
    }
    
    private fun calculateTimeRemaining(dateUtc: String): TimeRemaining {
        try {
            val launchTime = Instant.parse(dateUtc)
            val now = Instant.now()
            val duration = Duration.between(now, launchTime)
            
            if (duration.isNegative || duration.isZero) {
                return TimeRemaining(0, 0, 0, 0)
            }
            
            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60
            
            return TimeRemaining(days, hours, minutes, seconds)
        } catch (e: Exception) {
            return TimeRemaining(0, 0, 0, 0)
        }
    }
    
    private fun formatTimeRemaining(timeRemaining: TimeRemaining): String {
        return if (timeRemaining.days == 0L && timeRemaining.hours == 0L && 
                  timeRemaining.minutes == 0L && timeRemaining.seconds == 0L) {
            "🚀 ¡LANZAMIENTO AHORA!"
        } else {
            "${timeRemaining.days}d ${timeRemaining.hours}h ${timeRemaining.minutes}m ${timeRemaining.seconds}s"
        }
    }
}