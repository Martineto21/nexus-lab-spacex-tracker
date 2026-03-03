package com.nexuslab.spacextracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.model.Launchpad
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchpadViewModel @Inject constructor(
    private val repository: SpaceXRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LaunchpadUiState())
    val uiState: StateFlow<LaunchpadUiState> = _uiState.asStateFlow()
    
    init {
        loadLaunchpads()
    }
    
    private fun loadLaunchpads() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getAllLaunchpads()
                .onSuccess { launchpads ->
                    _uiState.value = _uiState.value.copy(
                        launchpads = launchpads,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error cargando launchpads: ${exception.message}"
                    )
                }
        }
    }
    
    fun selectLaunchpad(launchpad: Launchpad?) {
        _uiState.value = _uiState.value.copy(selectedLaunchpad = launchpad)
    }
    
    fun retryLoading() {
        loadLaunchpads()
    }
}

data class LaunchpadUiState(
    val launchpads: List<Launchpad> = emptyList(),
    val selectedLaunchpad: Launchpad? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)