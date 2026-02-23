package com.nexuslab.spacextracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LaunchesUiState(
    val launches: List<Launch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LaunchesViewModel : ViewModel() {
    
    private val repository = SpaceXRepository()
    
    private val _uiState = MutableStateFlow(LaunchesUiState())
    val uiState: StateFlow<LaunchesUiState> = _uiState.asStateFlow()
    
    init {
        loadLaunches()
    }
    
    fun loadLaunches() {
        viewModelScope.launch {
            Log.d("LaunchesViewModel", "🚀 Starting to load launches from SpaceX API...")
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getAllLaunches()
                .onSuccess { launches ->
                    Log.d("LaunchesViewModel", "✅ Successfully loaded ${launches.size} launches!")
                    _uiState.value = _uiState.value.copy(
                        launches = launches,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    Log.e("LaunchesViewModel", "❌ Failed to load launches: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
        }
    }
    
    fun loadLatestLaunch() {
        viewModelScope.launch {
            Log.d("LaunchesViewModel", "🚀 Loading latest launch...")
            
            repository.getLatestLaunch()
                .onSuccess { launch ->
                    Log.d("LaunchesViewModel", "✅ Latest launch: ${launch.name} on ${launch.dateUtc}")
                }
                .onFailure { exception ->
                    Log.e("LaunchesViewModel", "❌ Failed to load latest launch: ${exception.message}")
                }
        }
    }
    
    fun loadNextLaunch() {
        viewModelScope.launch {
            Log.d("LaunchesViewModel", "🚀 Loading next launch...")
            
            repository.getNextLaunch()
                .onSuccess { launch ->
                    Log.d("LaunchesViewModel", "✅ Next launch: ${launch.name} scheduled for ${launch.dateUtc}")
                }
                .onFailure { exception ->
                    Log.e("LaunchesViewModel", "❌ Failed to load next launch: ${exception.message}")
                }
        }
    }
}