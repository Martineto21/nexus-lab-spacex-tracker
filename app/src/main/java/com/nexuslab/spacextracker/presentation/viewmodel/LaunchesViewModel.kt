package com.nexuslab.spacextracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import com.nexuslab.spacextracker.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LaunchesUiState(
    val launches: List<Launch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false,
    val lastSyncTime: Long = 0L
)

@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val repository: SpaceXRepository,
    private val syncManager: SyncManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LaunchesUiState())
    val uiState: StateFlow<LaunchesUiState> = _uiState.asStateFlow()
    
    init {
        setupLaunchesObserver()
        loadLaunches()
    }
    
    private fun setupLaunchesObserver() {
        viewModelScope.launch {
            repository.getAllLaunchesFlow()
                .catch { exception ->
                    Log.e("LaunchesViewModel", "❌ Error observing launches: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Unknown error",
                        isLoading = false
                    )
                }
                .collect { launches ->
                    Log.d("LaunchesViewModel", "📱 Received ${launches.size} launches from cache/DB")
                    _uiState.value = _uiState.value.copy(
                        launches = launches,
                        isLoading = false,
                        error = null,
                        lastSyncTime = System.currentTimeMillis()
                    )
                }
        }
    }
    
    fun loadLaunches() {
        viewModelScope.launch {
            Log.d("LaunchesViewModel", "🚀 Starting to load launches (offline-first)...")
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // This will try network first, fallback to cache
            repository.getAllLaunches()
                .onSuccess { launches ->
                    Log.d("LaunchesViewModel", "✅ Successfully loaded ${launches.size} launches!")
                    // UI will be updated automatically via Flow
                }
                .onFailure { exception ->
                    Log.e("LaunchesViewModel", "❌ Failed to load launches: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error",
                        isOffline = true
                    )
                }
        }
    }
    
    fun refreshData() {
        viewModelScope.launch {
            Log.d("LaunchesViewModel", "🔄 Manual refresh triggered...")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Trigger background sync
            syncManager.syncNow()
            
            // Also try to refresh immediately
            repository.refreshAllData()
            
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isOffline = false)
    }
    
    fun getUpcomingLaunches(): StateFlow<List<Launch>> {
        val upcomingFlow = MutableStateFlow<List<Launch>>(emptyList())
        
        viewModelScope.launch {
            repository.getUpcomingLaunchesFlow()
                .catch { exception ->
                    Log.e("LaunchesViewModel", "❌ Error loading upcoming launches: ${exception.message}")
                }
                .collect { launches ->
                    upcomingFlow.value = launches
                }
        }
        
        return upcomingFlow.asStateFlow()
    }
    
    fun getPastLaunches(): StateFlow<List<Launch>> {
        val pastFlow = MutableStateFlow<List<Launch>>(emptyList())
        
        viewModelScope.launch {
            repository.getPastLaunchesFlow()
                .catch { exception ->
                    Log.e("LaunchesViewModel", "❌ Error loading past launches: ${exception.message}")
                }
                .collect { launches ->
                    pastFlow.value = launches
                }
        }
        
        return pastFlow.asStateFlow()
    }
}