package com.nexuslab.spacextracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.repository.SpaceXRepository

/**
 * LaunchDetailViewModel - ViewModel para pantalla de detalle
 * 
 * Generado para Día 4 del curso Nexus Lab - 25/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * Gestiona:
 * - Carga de detalle específico del lanzamiento
 * - Estados de carga y error
 * - Datos adicionales para la pantalla de detalle
 */

data class LaunchDetailUiState(
    val isLoading: Boolean = false,
    val launch: Launch? = null,
    val error: String? = null
)

class LaunchDetailViewModel : ViewModel() {
    private val repository = SpaceXRepository()
    
    private val _uiState = MutableStateFlow(LaunchDetailUiState())
    val uiState: StateFlow<LaunchDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Cargar detalle específico de un lanzamiento
     */
    fun loadLaunchDetail(launchId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                val launch = repository.getLaunchById(launchId)
                if (launch != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        launch = launch,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        launch = null,
                        error = "Lanzamiento no encontrado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    launch = null,
                    error = "Error al cargar el detalle: ${e.localizedMessage}"
                )
            }
        }
    }
    
    /**
     * Limpiar el estado del detalle
     */
    fun clearDetail() {
        _uiState.value = LaunchDetailUiState()
    }
}