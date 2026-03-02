package com.nexuslab.spacextracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.SpaceXStatistics
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: SpaceXRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getStatistics()
                .onSuccess { statistics ->
                    Log.d("StatisticsViewModel", "✅ Statistics loaded successfully")
                    Log.d("StatisticsViewModel", "📊 Total launches: ${statistics.totalLaunches}")
                    Log.d("StatisticsViewModel", "✅ Success rate: ${statistics.successRate}%")
                    
                    _uiState.value = _uiState.value.copy(
                        statistics = statistics,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e("StatisticsViewModel", "❌ Failed to load statistics: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
        }
    }
    
    fun refreshStatistics() {
        loadStatistics()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StatisticsUiState(
    val statistics: SpaceXStatistics = SpaceXStatistics(),
    val isLoading: Boolean = false,
    val error: String? = null
)