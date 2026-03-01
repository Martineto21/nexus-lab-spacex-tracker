package com.nexuslab.spacextracker.di

import android.content.Context
import com.nexuslab.spacextracker.data.preferences.ThemePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias - Generado por Nexus Platform
 * 
 * Proporciona las dependencias globales de la aplicación:
 * - Preferencias de tema
 * - Contexto de aplicación
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }
}