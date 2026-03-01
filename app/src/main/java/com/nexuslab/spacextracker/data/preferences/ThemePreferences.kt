package com.nexuslab.spacextracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestión de preferencias de tema - Generado por Nexus Platform
 * 
 * Maneja la persistencia de preferencias de UI:
 * - Modo oscuro/claro
 * - Colores dinámicos
 * - Configuración visual
 */

// Extensión para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemePreferences @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")
    }

    /**
     * Flow de configuración de tema oscuro
     * null = seguir sistema, true = forzar oscuro, false = forzar claro
     */
    val isDarkTheme: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        if (preferences[PreferencesKeys.FOLLOW_SYSTEM] != false) {
            null // Seguir sistema
        } else {
            preferences[PreferencesKeys.DARK_THEME] ?: true // Por defecto oscuro (SpaceX style)
        }
    }

    /**
     * Flow de colores dinámicos (Material You)
     */
    val isDynamicColorEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DYNAMIC_COLOR] ?: false // Por defecto false para mantener branding SpaceX
    }

    /**
     * Flow que indica si debe seguir el sistema
     */
    val shouldFollowSystem: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FOLLOW_SYSTEM] ?: true // Por defecto seguir sistema
    }

    /**
     * Establecer modo de tema
     * @param darkTheme true = oscuro, false = claro, null = seguir sistema
     */
    suspend fun setDarkTheme(darkTheme: Boolean?) {
        context.dataStore.edit { preferences ->
            if (darkTheme == null) {
                preferences[PreferencesKeys.FOLLOW_SYSTEM] = true
                preferences.remove(PreferencesKeys.DARK_THEME)
            } else {
                preferences[PreferencesKeys.FOLLOW_SYSTEM] = false
                preferences[PreferencesKeys.DARK_THEME] = darkTheme
            }
        }
    }

    /**
     * Alternar tema manualmente
     */
    suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            val currentDarkTheme = preferences[PreferencesKeys.DARK_THEME] ?: true
            preferences[PreferencesKeys.FOLLOW_SYSTEM] = false
            preferences[PreferencesKeys.DARK_THEME] = !currentDarkTheme
        }
    }

    /**
     * Habilitar/deshabilitar colores dinámicos
     */
    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    /**
     * Volver a seguir el tema del sistema
     */
    suspend fun followSystemTheme() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOLLOW_SYSTEM] = true
            preferences.remove(PreferencesKeys.DARK_THEME)
        }
    }
}