package com.nexuslab.spacextracker.data.repository

import android.util.Log
import com.nexuslab.spacextracker.data.api.SpaceXApiService
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Rocket
import com.nexuslab.spacextracker.data.network.NetworkModule

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
}