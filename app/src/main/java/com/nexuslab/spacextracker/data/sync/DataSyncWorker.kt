package com.nexuslab.spacextracker.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexuslab.spacextracker.data.repository.SpaceXRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SpaceXRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DataSyncWorker", "🔄 Starting background data sync...")
            
            // Clean old cache first
            repository.cleanOldCache()
            
            // Refresh all data
            repository.refreshAllData()
            
            val (launches, rockets, launchpads) = repository.getCacheStats()
            Log.d("DataSyncWorker", "✅ Sync completed: $launches launches, $rockets rockets, $launchpads launchpads")
            
            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "❌ Sync failed: ${e.message}", e)
            // Retry on failure
            Result.retry()
        }
    }
    
    companion object {
        const val WORK_NAME = "spacex_data_sync"
    }
}