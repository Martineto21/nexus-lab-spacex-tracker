package com.nexuslab.spacextracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for SpaceX Tracker - Generado por Nexus Platform
 * 
 * Configuración global de la aplicación:
 * - Inicialización de Hilt para inyección de dependencias
 * - Configuración de servicios globales
 * 
 * Actualizada el 01/03/2026 - Día 8: Dark Mode + Theming SpaceX
 */
@HiltAndroidApp
class SpaceXTrackerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización adicional si es necesaria
    }
}