package com.nexuslab.spacextracker.data.repository

import android.util.Log
import com.nexuslab.spacextracker.data.api.SpaceXApiService
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Launchpad
import com.nexuslab.spacextracker.data.model.Rocket
import com.nexuslab.spacextracker.data.model.SpaceXStatistics
import com.nexuslab.spacextracker.data.model.YearlyStats
import com.nexuslab.spacextracker.data.model.RocketStats
import com.nexuslab.spacextracker.data.network.NetworkModule
import kotlinx.datetime.Instant

class SpaceXRepository {
    
    private val apiService: SpaceXApiService = NetworkModule.spaceXApiService
    
    suspend fun getAllLaunches(): Result<List<Launch>> {
        return try {
            val response = apiService.getAllLaunches()
            if (response.isSuccessful) {
                val launches = response.body() ?: emptyList()
                Log.d("SpaceXRepository", "✅ Loaded ${launches.size} launches successfully")
                
                // Log some sample data
                launches.take(3).forEach { launch ->
                    Log.d("SpaceXRepository", "🚀 Launch: ${launch.name} - ${launch.dateUtc} - Success: ${launch.success}")
                }
                
                Result.success(launches)
            } else {
                val error = "API Error: ${response.code()} - ${response.message()}"
                Log.e("SpaceXRepository", "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Network error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getLatestLaunch(): Result<Launch> {
        return try {
            val response = apiService.getLatestLaunch()
            if (response.isSuccessful) {
                val launch = response.body()
                if (launch != null) {
                    Log.d("SpaceXRepository", "✅ Latest launch: ${launch.name}")
                    Result.success(launch)
                } else {
                    Result.failure(Exception("No launch data received"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting latest launch: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getNextLaunch(): Result<Launch> {
        return try {
            val response = apiService.getNextLaunch()
            if (response.isSuccessful) {
                val launch = response.body()
                if (launch != null) {
                    Log.d("SpaceXRepository", "✅ Next launch: ${launch.name} at ${launch.dateUtc}")
                    Result.success(launch)
                } else {
                    Result.failure(Exception("No upcoming launch data"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting next launch: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getLaunchById(id: String): Launch? {
        return try {
            val response = apiService.getLaunchById(id)
            if (response.isSuccessful) {
                val launch = response.body()
                Log.d("SpaceXRepository", "✅ Loaded launch detail: ${launch?.name}")
                launch
            } else {
                Log.e("SpaceXRepository", "❌ Error getting launch by ID: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launch by ID: ${e.message}", e)
            null
        }
    }
    
    suspend fun getAllRockets(): Result<List<Rocket>> {
        return try {
            val response = apiService.getAllRockets()
            if (response.isSuccessful) {
                val rockets = response.body() ?: emptyList()
                Log.d("SpaceXRepository", "✅ Loaded ${rockets.size} rockets successfully")
                
                rockets.forEach { rocket ->
                    Log.d("SpaceXRepository", "🚀 Rocket: ${rocket.name} - Active: ${rocket.active} - Success rate: ${rocket.successRatePct}%")
                }
                
                Result.success(rockets)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting rockets: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getAllLaunchpads(): Result<List<Launchpad>> {
        return try {
            val response = apiService.getAllLaunchpads()
            if (response.isSuccessful) {
                val launchpads = response.body() ?: emptyList()
                Log.d("SpaceXRepository", "✅ Loaded ${launchpads.size} launchpads successfully")
                
                launchpads.forEach { pad ->
                    Log.d("SpaceXRepository", "🚁 Launchpad: ${pad.name} (${pad.locality}) - " +
                            "Lat: ${pad.latitude}, Lng: ${pad.longitude} - " +
                            "Success rate: ${pad.successRate}%")
                }
                
                Result.success(launchpads)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launchpads: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getLaunchpadById(id: String): Launchpad? {
        return try {
            val response = apiService.getLaunchpadById(id)
            if (response.isSuccessful) {
                val launchpad = response.body()
                Log.d("SpaceXRepository", "✅ Loaded launchpad detail: ${launchpad?.name}")
                launchpad
            } else {
                Log.e("SpaceXRepository", "❌ Error getting launchpad by ID: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launchpad by ID: ${e.message}", e)
            null
        }
    }
    
    suspend fun getStatistics(): Result<SpaceXStatistics> {
        return try {
            val launchesResult = getAllLaunches()
            val rocketsResult = getAllRockets()
            
            if (launchesResult.isSuccess && rocketsResult.isSuccess) {
                val launches = launchesResult.getOrNull() ?: emptyList()
                val rockets = rocketsResult.getOrNull() ?: emptyList()
                
                val statistics = calculateStatistics(launches, rockets)
                Log.d("SpaceXRepository", "✅ Statistics calculated successfully")
                Result.success(statistics)
            } else {
                Result.failure(Exception("Failed to load data for statistics"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error calculating statistics: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    private fun calculateStatistics(launches: List<Launch>, rockets: List<Rocket>): SpaceXStatistics {
        val totalLaunches = launches.size
        val successfulLaunches = launches.count { it.success == true }
        val failedLaunches = launches.count { it.success == false }
        
        // Contar boosters recuperados (aproximación basada en datos disponibles)
        val boostersRecovered = launches.count { 
            it.cores?.any { core -> core.landingSuccess == true } == true
        }
        val boostersLost = launches.count { 
            it.cores?.any { core -> core.landingSuccess == false } == true
        }
        
        val successRate = if (totalLaunches > 0) {
            (successfulLaunches.toFloat() / totalLaunches.toFloat()) * 100f
        } else 0f
        
        // Estadísticas por año
        val launchesPerYear = launches
            .mapNotNull { launch ->
                try {
                    val instant = Instant.parse(launch.dateUtc)
                    val year = instant.toString().substring(0, 4).toInt()
                    Pair(year, launch)
                } catch (e: Exception) {
                    null
                }
            }
            .groupBy { it.first }
            .map { (year, launches) ->
                val yearLaunches = launches.map { it.second }
                YearlyStats(
                    year = year,
                    launches = yearLaunches.size,
                    successes = yearLaunches.count { it.success == true },
                    failures = yearLaunches.count { it.success == false }
                )
            }
            .sortedBy { it.year }
        
        // Estadísticas por cohete
        val rocketStats = launches
            .groupBy { it.rocket }
            .mapNotNull { (rocketId, launchList) ->
                val rocket = rockets.find { it.id == rocketId }
                if (rocket != null) {
                    RocketStats(
                        rocketName = rocket.name,
                        launches = launchList.size,
                        successes = launchList.count { it.success == true },
                        failures = launchList.count { it.success == false },
                        color = getRocketColor(rocket.name)
                    )
                } else null
            }
            .sortedByDescending { it.launches }
        
        return SpaceXStatistics(
            totalLaunches = totalLaunches,
            successfulLaunches = successfulLaunches,
            failedLaunches = failedLaunches,
            boostersRecovered = boostersRecovered,
            boostersLost = boostersLost,
            successRate = successRate,
            launchesPerYear = launchesPerYear,
            rocketStats = rocketStats
        )
    }
    
    private fun getRocketColor(rocketName: String): String {
        return when (rocketName.lowercase()) {
            "falcon 1" -> "#FF5722"      // Naranja
            "falcon 9" -> "#2196F3"      // Azul
            "falcon heavy" -> "#4CAF50"  // Verde
            "starship" -> "#9C27B0"      // Púrpura
            else -> "#607D8B"            // Gris azulado
        }
    }
}