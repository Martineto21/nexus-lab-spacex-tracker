package com.nexuslab.spacextracker.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo para estadísticas de SpaceX
 */
@Serializable
data class SpaceXStatistics(
    val totalLaunches: Int = 0,
    val successfulLaunches: Int = 0,
    val failedLaunches: Int = 0,
    val boostersRecovered: Int = 0,
    val boostersLost: Int = 0,
    val successRate: Float = 0f,
    val launchesPerYear: List<YearlyStats> = emptyList(),
    val rocketStats: List<RocketStats> = emptyList()
)

@Serializable
data class YearlyStats(
    val year: Int,
    val launches: Int,
    val successes: Int,
    val failures: Int
)

@Serializable
data class RocketStats(
    val rocketName: String,
    val launches: Int,
    val successes: Int,
    val failures: Int,
    val color: String = "#1976D2" // Azul por defecto
)

@Serializable
data class ChartDataPoint(
    val label: String,
    val value: Float,
    val color: String = "#1976D2"
)