package com.nexuslab.spacextracker.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    fun startPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val syncWorkRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            repeatInterval = 2, // Every 2 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            DataSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
        
        Log.d("SyncManager", "📅 Periodic sync scheduled (every 2 hours)")
    }
    
    fun stopPeriodicSync() {
        workManager.cancelUniqueWork(DataSyncWorker.WORK_NAME)
        Log.d("SyncManager", "⏹️ Periodic sync cancelled")
    }
    
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncWorkRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
            repeatInterval = 15, // Minimum interval
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        workManager.enqueue(syncWorkRequest)
        Log.d("SyncManager", "🚀 Manual sync triggered")
    }
}