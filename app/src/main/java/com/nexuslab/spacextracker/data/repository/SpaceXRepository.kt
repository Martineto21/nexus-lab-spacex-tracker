package com.nexuslab.spacextracker.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.nexuslab.spacextracker.data.api.SpaceXApiService
import com.nexuslab.spacextracker.data.database.SpaceXDatabase
import com.nexuslab.spacextracker.data.database.entity.LaunchEntity
import com.nexuslab.spacextracker.data.database.entity.LaunchpadEntity
import com.nexuslab.spacextracker.data.database.entity.RocketEntity
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Launchpad
import com.nexuslab.spacextracker.data.model.Rocket
import com.nexuslab.spacextracker.data.model.SpaceXStatistics
import com.nexuslab.spacextracker.data.model.YearlyStats
import com.nexuslab.spacextracker.data.model.RocketStats
import com.nexuslab.spacextracker.data.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpaceXRepository @Inject constructor(
    private val context: Context,
    private val database: SpaceXDatabase
) {
    
    private val apiService: SpaceXApiService = NetworkModule.spaceXApiService
    
    // Cache expiry time: 1 hour
    private val cacheExpiryTime = 60 * 60 * 1000L
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    private fun isCacheValid(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated < cacheExpiryTime
    }
    
    // Flow-based methods for reactive UI updates
    fun getAllLaunchesFlow(): Flow<List<Launch>> {
        return database.launchDao().getAllLaunches().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getUpcomingLaunchesFlow(): Flow<List<Launch>> {
        return database.launchDao().getUpcomingLaunches().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getPastLaunchesFlow(): Flow<List<Launch>> {
        return database.launchDao().getPastLaunches().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getAllLaunches(): Result<List<Launch>> {
        return try {
            // First, try to get cached data
            val cachedLaunches = database.launchDao().getAllLaunches().first()
            val recentCacheCount = database.launchDao().getRecentLaunchesCount(
                System.currentTimeMillis() - cacheExpiryTime
            )
            
            // If cache is valid and not empty, use it
            if (recentCacheCount > 0) {
                Log.d("SpaceXRepository", "📱 Using cached launches ($recentCacheCount items)")
                val launches = cachedLaunches.map { it.toDomain() }
                return Result.success(launches)
            }
            
            // If network is available, fetch fresh data
            if (isNetworkAvailable()) {
                Log.d("SpaceXRepository", "🌐 Fetching fresh launches from API...")
                val response = apiService.getAllLaunches()
                if (response.isSuccessful) {
                    val launches = response.body() ?: emptyList()
                    Log.d("SpaceXRepository", "✅ Loaded ${launches.size} launches successfully")
                    
                    // Cache the data
                    val entities = launches.map { LaunchEntity.fromDomain(it) }
                    database.launchDao().insertLaunches(entities)
                    Log.d("SpaceXRepository", "💾 Cached ${entities.size} launches")
                    
                    // Log some sample data
                    launches.take(3).forEach { launch ->
                        Log.d("SpaceXRepository", "🚀 Launch: ${launch.name} - ${launch.dateUtc} - Success: ${launch.success}")
                    }
                    
                    return Result.success(launches)
                } else {
                    Log.e("SpaceXRepository", "❌ API Error: ${response.code()} - ${response.message()}")
                    // Fallback to any cached data
                    val fallbackLaunches = cachedLaunches.map { it.toDomain() }
                    return Result.success(fallbackLaunches)
                }
            } else {
                // No network, use any cached data available
                Log.w("SpaceXRepository", "📵 No network, using any cached launches available")
                val fallbackLaunches = cachedLaunches.map { it.toDomain() }
                return Result.success(fallbackLaunches)
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error in getAllLaunches: ${e.message}", e)
            // Last resort: try to get any cached data
            try {
                val cachedLaunches = database.launchDao().getAllLaunches().first()
                val fallbackLaunches = cachedLaunches.map { it.toDomain() }
                Log.w("SpaceXRepository", "🔄 Using fallback cached data")
                return Result.success(fallbackLaunches)
            } catch (cacheError: Exception) {
                Log.e("SpaceXRepository", "❌ Cache fallback failed: ${cacheError.message}")
                return Result.failure(e)
            }
        }
    }
    
    suspend fun getLatestLaunch(): Result<Launch> {
        return try {
            // Try network first, fallback to cached data
            if (isNetworkAvailable()) {
                val response = apiService.getLatestLaunch()
                if (response.isSuccessful) {
                    val launch = response.body()
                    if (launch != null) {
                        // Cache the launch
                        database.launchDao().insertLaunch(LaunchEntity.fromDomain(launch))
                        Log.d("SpaceXRepository", "✅ Latest launch: ${launch.name}")
                        return Result.success(launch)
                    }
                }
            }
            
            // Fallback: get most recent past launch from cache
            val launches = database.launchDao().getPastLaunches().first()
            val latestCached = launches.firstOrNull()?.toDomain()
            if (latestCached != null) {
                Log.d("SpaceXRepository", "📱 Using cached latest launch: ${latestCached.name}")
                Result.success(latestCached)
            } else {
                Result.failure(Exception("No launch data available"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting latest launch: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getNextLaunch(): Result<Launch> {
        return try {
            // Check cache first
            val cachedNext = database.launchDao().getNextLaunch()
            if (cachedNext != null && isCacheValid(cachedNext.lastUpdated)) {
                Log.d("SpaceXRepository", "📱 Using cached next launch: ${cachedNext.name}")
                return Result.success(cachedNext.toDomain())
            }
            
            // Try network
            if (isNetworkAvailable()) {
                val response = apiService.getNextLaunch()
                if (response.isSuccessful) {
                    val launch = response.body()
                    if (launch != null) {
                        // Cache the launch
                        database.launchDao().insertLaunch(LaunchEntity.fromDomain(launch))
                        Log.d("SpaceXRepository", "✅ Next launch: ${launch.name} at ${launch.dateUtc}")
                        return Result.success(launch)
                    }
                }
            }
            
            // Fallback to any cached next launch
            if (cachedNext != null) {
                Log.d("SpaceXRepository", "🔄 Using stale cached next launch: ${cachedNext.name}")
                Result.success(cachedNext.toDomain())
            } else {
                Result.failure(Exception("No upcoming launch data available"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting next launch: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getLaunchByIdFlow(id: String): Flow<Launch?> {
        return database.launchDao().getLaunchByIdFlow(id).map { entity ->
            entity?.toDomain()
        }
    }
    
    suspend fun getLaunchById(id: String): Launch? {
        return try {
            // Check cache first
            val cached = database.launchDao().getLaunchById(id)
            if (cached != null && isCacheValid(cached.lastUpdated)) {
                Log.d("SpaceXRepository", "📱 Using cached launch detail: ${cached.name}")
                return cached.toDomain()
            }
            
            // Try network
            if (isNetworkAvailable()) {
                val response = apiService.getLaunchById(id)
                if (response.isSuccessful) {
                    val launch = response.body()
                    if (launch != null) {
                        // Cache the launch
                        database.launchDao().insertLaunch(LaunchEntity.fromDomain(launch))
                        Log.d("SpaceXRepository", "✅ Loaded launch detail: ${launch.name}")
                        return launch
                    }
                }
            }
            
            // Fallback to stale cached data
            cached?.toDomain().also {
                if (it != null) {
                    Log.d("SpaceXRepository", "🔄 Using stale cached launch: ${it.name}")
                }
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launch by ID: ${e.message}", e)
            // Last resort: return cached data if available
            database.launchDao().getLaunchById(id)?.toDomain()
        }
    }
    
    fun getAllRocketsFlow(): Flow<List<Rocket>> {
        return database.rocketDao().getAllRockets().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getAllRockets(): Result<List<Rocket>> {
        return try {
            // Check cache first
            val cachedRockets = database.rocketDao().getAllRockets().first()
            val rocketCount = database.rocketDao().getTotalRocketsCount()
            
            // Rockets change rarely, so cache for longer (4 hours)
            val extendedCacheTime = 4 * 60 * 60 * 1000L
            val hasValidCache = rocketCount > 0 // Rockets are relatively static
            
            if (hasValidCache && !isNetworkAvailable()) {
                Log.d("SpaceXRepository", "📱 Using cached rockets ($rocketCount items)")
                val rockets = cachedRockets.map { it.toDomain() }
                return Result.success(rockets)
            }
            
            // Try network
            if (isNetworkAvailable()) {
                Log.d("SpaceXRepository", "🌐 Fetching fresh rockets from API...")
                val response = apiService.getAllRockets()
                if (response.isSuccessful) {
                    val rockets = response.body() ?: emptyList()
                    Log.d("SpaceXRepository", "✅ Loaded ${rockets.size} rockets successfully")
                    
                    // Cache the data
                    val entities = rockets.map { RocketEntity.fromDomain(it) }
                    database.rocketDao().insertRockets(entities)
                    Log.d("SpaceXRepository", "💾 Cached ${entities.size} rockets")
                    
                    rockets.forEach { rocket ->
                        Log.d("SpaceXRepository", "🚀 Rocket: ${rocket.name} - Active: ${rocket.active} - Success rate: ${rocket.successRatePct}%")
                    }
                    
                    return Result.success(rockets)
                }
            }
            
            // Fallback to cached data
            Log.w("SpaceXRepository", "🔄 Using cached rockets as fallback")
            val fallbackRockets = cachedRockets.map { it.toDomain() }
            Result.success(fallbackRockets)
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting rockets: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getAllLaunchpadsFlow(): Flow<List<Launchpad>> {
        return database.launchpadDao().getAllLaunchpads().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getAllLaunchpads(): Result<List<Launchpad>> {
        return try {
            // Check cache first
            val cachedLaunchpads = database.launchpadDao().getAllLaunchpads().first()
            val launchpadCount = database.launchpadDao().getTotalLaunchpadsCount()
            
            // Launchpads change rarely, use cache when available
            if (launchpadCount > 0 && !isNetworkAvailable()) {
                Log.d("SpaceXRepository", "📱 Using cached launchpads ($launchpadCount items)")
                val launchpads = cachedLaunchpads.map { it.toDomain() }
                return Result.success(launchpads)
            }
            
            // Try network
            if (isNetworkAvailable()) {
                Log.d("SpaceXRepository", "🌐 Fetching fresh launchpads from API...")
                val response = apiService.getAllLaunchpads()
                if (response.isSuccessful) {
                    val launchpads = response.body() ?: emptyList()
                    Log.d("SpaceXRepository", "✅ Loaded ${launchpads.size} launchpads successfully")
                    
                    // Cache the data
                    val entities = launchpads.map { LaunchpadEntity.fromDomain(it) }
                    database.launchpadDao().insertLaunchpads(entities)
                    Log.d("SpaceXRepository", "💾 Cached ${entities.size} launchpads")
                    
                    launchpads.forEach { pad ->
                        Log.d("SpaceXRepository", "🚁 Launchpad: ${pad.name} (${pad.locality}) - " +
                                "Lat: ${pad.latitude}, Lng: ${pad.longitude}")
                    }
                    
                    return Result.success(launchpads)
                }
            }
            
            // Fallback to cached data
            Log.w("SpaceXRepository", "🔄 Using cached launchpads as fallback")
            val fallbackLaunchpads = cachedLaunchpads.map { it.toDomain() }
            Result.success(fallbackLaunchpads)
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launchpads: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun getLaunchpadByIdFlow(id: String): Flow<Launchpad?> {
        return database.launchpadDao().getLaunchpadByIdFlow(id).map { entity ->
            entity?.toDomain()
        }
    }
    
    suspend fun getLaunchpadById(id: String): Launchpad? {
        return try {
            // Check cache first
            val cached = database.launchpadDao().getLaunchpadById(id)
            if (cached != null && isCacheValid(cached.lastUpdated)) {
                Log.d("SpaceXRepository", "📱 Using cached launchpad: ${cached.name}")
                return cached.toDomain()
            }
            
            // Try network
            if (isNetworkAvailable()) {
                val response = apiService.getLaunchpadById(id)
                if (response.isSuccessful) {
                    val launchpad = response.body()
                    if (launchpad != null) {
                        // Cache the launchpad
                        database.launchpadDao().insertLaunchpad(LaunchpadEntity.fromDomain(launchpad))
                        Log.d("SpaceXRepository", "✅ Loaded launchpad detail: ${launchpad.name}")
                        return launchpad
                    }
                }
            }
            
            // Fallback to stale cached data
            cached?.toDomain().also {
                if (it != null) {
                    Log.d("SpaceXRepository", "🔄 Using stale cached launchpad: ${it.name}")
                }
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error getting launchpad by ID: ${e.message}", e)
            database.launchpadDao().getLaunchpadById(id)?.toDomain()
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
                Log.d("SpaceXRepository", "✅ Statistics calculated successfully (${launches.size} launches, ${rockets.size} rockets)")
                Result.success(statistics)
            } else {
                Result.failure(Exception("Failed to load data for statistics"))
            }
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error calculating statistics: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Cache management utilities
    suspend fun refreshAllData() {
        try {
            Log.d("SpaceXRepository", "🔄 Refreshing all cached data...")
            getAllLaunches()
            getAllRockets()  
            getAllLaunchpads()
            Log.d("SpaceXRepository", "✅ All data refreshed successfully")
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error refreshing data: ${e.message}", e)
        }
    }
    
    suspend fun clearCache() {
        try {
            database.launchDao().deleteAllLaunches()
            database.rocketDao().deleteAllRockets()
            database.launchpadDao().deleteAllLaunchpads()
            Log.d("SpaceXRepository", "🗑️ Cache cleared successfully")
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error clearing cache: ${e.message}", e)
        }
    }
    
    suspend fun cleanOldCache() {
        try {
            val cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L) // 7 days
            database.launchDao().deleteOldLaunches(cutoffTime)
            database.rocketDao().deleteOldRockets(cutoffTime)
            database.launchpadDao().deleteOldLaunchpads(cutoffTime)
            Log.d("SpaceXRepository", "🧹 Old cache cleaned successfully")
        } catch (e: Exception) {
            Log.e("SpaceXRepository", "❌ Error cleaning old cache: ${e.message}", e)
        }
    }
    
    suspend fun getCacheStats(): Triple<Int, Int, Int> {
        return try {
            val launchCount = database.launchDao().getTotalLaunchesCount()
            val rocketCount = database.rocketDao().getTotalRocketsCount()
            val launchpadCount = database.launchpadDao().getTotalLaunchpadsCount()
            Triple(launchCount, rocketCount, launchpadCount)
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }
    }
    
    private fun calculateStatistics(launches: List<Launch>, rockets: List<Rocket>): SpaceXStatistics {
        val totalLaunches = launches.size
        val successfulLaunches = launches.count { it.success == true }
        val failedLaunches = launches.count { it.success == false }
        
        // Contar boosters recuperados (aproximación basada en datos disponibles)
        val boostersRecovered = 0
        val boostersLost = 0
        
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
            .groupBy { it.rocketId }
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
