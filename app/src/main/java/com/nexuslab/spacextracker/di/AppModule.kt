package com.nexuslab.spacextracker.di

import android.content.Context
import com.nexuslab.spacextracker.data.database.SpaceXDatabase
import com.nexuslab.spacextracker.data.preferences.ThemePreferences
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
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
 * - Base de datos Room
 * - Repositorio con caché offline
 * - Preferencias de tema
 * - Contexto de aplicación
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSpaceXDatabase(@ApplicationContext context: Context): SpaceXDatabase {
        return SpaceXDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSpaceXRepository(
        @ApplicationContext context: Context,
        database: SpaceXDatabase
    ): SpaceXRepository {
        return SpaceXRepository(context, database)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences {
        return ThemePreferences(context)
    }
}