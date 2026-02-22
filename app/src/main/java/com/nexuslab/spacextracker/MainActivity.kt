package com.nexuslab.spacextracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nexuslab.spacextracker.ui.SpaceXApp
import com.nexuslab.spacextracker.ui.theme.SpaceXTrackerTheme

/**
 * MainActivity - Punto de entrada principal de la app
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Agente: Upe (Memoria Viva de Rafa)
 * 
 * Esta actividad configura:
 * - Tema Material 3 personalizado SpaceX
 * - Navegación con Jetpack Compose
 * - Edge-to-edge display
 * - Scaffold con navegación inferior
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SpaceXTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpaceXApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}