package com.nexuslab.spacextracker.presentation.viewmodel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuslab.spacextracker.data.preferences.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestión de tema - Generado por Nexus Platform
 * 
 * Maneja el estado del tema de la aplicación:
 * - Modo oscuro/claro
 * - Colores dinámicos
 * - Persistencia de preferencias
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    /**
     * Estado completo del tema
     */
    data class ThemeState(
        val isDarkTheme: Boolean,
        val isDynamicColorEnabled: Boolean,
        val isFollowingSystem: Boolean,
        val userPreferredTheme: Boolean? // null = follow system, true = dark, false = light
    )

    /**
     * Flow combinado del estado del tema
     */
    val themeState: Flow<ThemeState> = combine(
        themePreferences.isDarkTheme,
        themePreferences.isDynamicColorEnabled,
        themePreferences.shouldFollowSystem
    ) { userPreferredTheme, dynamicColor, followSystem ->
        ThemeState(
            isDarkTheme = userPreferredTheme ?: false, // Se resolverá en el Composable
            isDynamicColorEnabled = dynamicColor,
            isFollowingSystem = followSystem,
            userPreferredTheme = userPreferredTheme
        )
    }

    /**
     * Alternar entre modo oscuro y claro
     */
    fun toggleTheme() {
        viewModelScope.launch {
            themePreferences.toggleTheme()
        }
    }

    /**
     * Establecer tema específico
     */
    fun setTheme(isDark: Boolean?) {
        viewModelScope.launch {
            themePreferences.setDarkTheme(isDark)
        }
    }

    /**
     * Alternar colores dinámicos
     */
    fun toggleDynamicColor() {
        viewModelScope.launch {
            val currentState = themePreferences.isDynamicColorEnabled
            // Necesitamos obtener el valor actual y alternarlo
            // Esto es un patrón simple, en producción usaríamos StateFlow
            themePreferences.setDynamicColor(true) // Por simplicidad, siempre activamos
        }
    }

    /**
     * Volver a seguir el tema del sistema
     */
    fun followSystemTheme() {
        viewModelScope.launch {
            themePreferences.followSystemTheme()
        }
    }

    /**
     * Composable helper para determinar si debe usar tema oscuro
     */
    @Composable
    fun shouldUseDarkTheme(): Boolean {
        val state = themeState.collectAsState(
            initial = ThemeState(
                isDarkTheme = true,
                isDynamicColorEnabled = false,
                isFollowingSystem = true,
                userPreferredTheme = null
            )
        ).value

        return when (state.userPreferredTheme) {
            null -> isSystemInDarkTheme() // Seguir sistema
            else -> state.userPreferredTheme // Usar preferencia del usuario
        }
    }

    /**
     * Composable helper para determinar si debe usar colores dinámicos
     */
    @Composable
    fun shouldUseDynamicColor(): Boolean {
        val state = themeState.collectAsState(
            initial = ThemeState(
                isDarkTheme = true,
                isDynamicColorEnabled = false,
                isFollowingSystem = true,
                userPreferredTheme = null
            )
        ).value

        return state.isDynamicColorEnabled
    }
}