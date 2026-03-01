package com.nexuslab.spacextracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nexuslab.spacextracker.presentation.viewmodel.ThemeViewModel
import com.nexuslab.spacextracker.ui.SpaceXApp
import com.nexuslab.spacextracker.ui.theme.SpaceXTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Punto de entrada principal de la app
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Actualizada el 01/03/2026 - Día 8: Dark Mode + Theming SpaceX
 * Agente: Upe (Memoria Viva de Rafa)
 * 
 * Esta actividad configura:
 * - Tema Material 3 personalizado SpaceX con soporte dark/light mode
 * - Navegación con Jetpack Compose
 * - Edge-to-edge display
 * - Scaffold con navegación inferior
 * - Gestión de preferencias de tema con Hilt
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SpaceXTrackerTheme(
                darkTheme = themeViewModel.shouldUseDarkTheme(),
                dynamicColor = themeViewModel.shouldUseDynamicColor()
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpaceXApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        themeViewModel = themeViewModel // Pasar el ViewModel para controles de tema
                    )
                }
            }
        }
    }
}