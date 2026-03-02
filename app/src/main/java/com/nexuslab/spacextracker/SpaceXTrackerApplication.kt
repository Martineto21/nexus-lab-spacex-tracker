package com.nexuslab.spacextracker

import android.app.Application
import com.nexuslab.spacextracker.data.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for SpaceX Tracker - Generado por Nexus Platform
 * 
 * Configuración global de la aplicación:
 * - Inicialización de Hilt para inyección de dependencias
 * - Configuración de servicios globales
 * - Sincronización automática en background
 * 
 * Actualizada el 02/03/2026 - Día 9: Offline Mode + Performance
 */
@HiltAndroidApp
class SpaceXTrackerApplication : Application() {
    
    @Inject
    lateinit var syncManager: SyncManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar sincronización automática
        syncManager.startPeriodicSync()
    }
}