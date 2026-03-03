package com.nexuslab.spacextracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import com.nexuslab.spacextracker.presentation.selectNextUpcomingLaunch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.time.Duration
import java.time.Instant
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

@HiltViewModel
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

                val endpointNextLaunch = repository.getNextLaunch().getOrNull()
                val fallbackNextLaunch = repository.getAllLaunches()
                    .getOrElse { emptyList() }
                    .let { launches -> selectNextUpcomingLaunch(launches, Clock.System.now()) }

                val nextLaunch = endpointNextLaunch ?: fallbackNextLaunch

                _state.value = _state.value.copy(
                    nextLaunch = nextLaunch,
                    timeRemaining = nextLaunch?.let { launch ->
                        formatTimeRemaining(calculateTimeRemaining(launch.dateUtc))
                    } ?: "",
                    isLoading = false,
                    error = if (nextLaunch == null) "No hay proximos lanzamientos disponibles" else null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar el proximo lanzamiento: ${e.message}"
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

                    if (timeRemaining.isZero()) {
                        loadNextLaunch()
                        delay(60_000)
                        continue
                    }
                }
                delay(1000)
            }
        }
    }

    private fun calculateTimeRemaining(dateUtc: String): TimeRemaining {
        return try {
            val launchTime = Instant.parse(dateUtc)
            val now = Instant.now()
            val duration = Duration.between(now, launchTime)

            if (duration.isNegative || duration.isZero) {
                TimeRemaining(0, 0, 0, 0)
            } else {
                TimeRemaining(
                    days = duration.toDays(),
                    hours = duration.toHours() % 24,
                    minutes = duration.toMinutes() % 60,
                    seconds = duration.seconds % 60
                )
            }
        } catch (_: Exception) {
            TimeRemaining(0, 0, 0, 0)
        }
    }

    private fun formatTimeRemaining(timeRemaining: TimeRemaining): String {
        return if (timeRemaining.isZero()) {
            "LANZAMIENTO AHORA"
        } else {
            "${timeRemaining.days}d ${timeRemaining.hours}h ${timeRemaining.minutes}m ${timeRemaining.seconds}s"
        }
    }

    private fun TimeRemaining.isZero(): Boolean {
        return days == 0L && hours == 0L && minutes == 0L && seconds == 0L
    }
}
