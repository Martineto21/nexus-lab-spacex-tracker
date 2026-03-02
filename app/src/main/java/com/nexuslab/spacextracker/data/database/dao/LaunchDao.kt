package com.nexuslab.spacextracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nexuslab.spacextracker.data.database.entity.LaunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchDao {
    
    @Query("SELECT * FROM launches ORDER BY dateUtc DESC")
    fun getAllLaunches(): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE upcoming = 1 ORDER BY dateUtc ASC")
    fun getUpcomingLaunches(): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE upcoming = 0 ORDER BY dateUtc DESC")
    fun getPastLaunches(): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE id = :id")
    suspend fun getLaunchById(id: String): LaunchEntity?
    
    @Query("SELECT * FROM launches WHERE id = :id")
    fun getLaunchByIdFlow(id: String): Flow<LaunchEntity?>
    
    @Query("SELECT * FROM launches WHERE name LIKE '%' || :query || '%' ORDER BY dateUtc DESC")
    fun searchLaunches(query: String): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE rocketId = :rocketId ORDER BY dateUtc DESC")
    fun getLaunchesByRocket(rocketId: String): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE launchpadId = :launchpadId ORDER BY dateUtc DESC")
    fun getLaunchesByLaunchpad(launchpadId: String): Flow<List<LaunchEntity>>
    
    @Query("SELECT * FROM launches WHERE upcoming = 1 ORDER BY dateUtc ASC LIMIT 1")
    suspend fun getNextLaunch(): LaunchEntity?
    
    @Query("SELECT COUNT(*) FROM launches WHERE success = 1")
    suspend fun getSuccessfulLaunchesCount(): Int
    
    @Query("SELECT COUNT(*) FROM launches WHERE success = 0")
    suspend fun getFailedLaunchesCount(): Int
    
    @Query("SELECT COUNT(*) FROM launches")
    suspend fun getTotalLaunchesCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunch(launch: LaunchEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunches(launches: List<LaunchEntity>)
    
    @Update
    suspend fun updateLaunch(launch: LaunchEntity)
    
    @Delete
    suspend fun deleteLaunch(launch: LaunchEntity)
    
    @Query("DELETE FROM launches")
    suspend fun deleteAllLaunches()
    
    @Query("DELETE FROM launches WHERE lastUpdated < :timestamp")
    suspend fun deleteOldLaunches(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM launches WHERE lastUpdated > :timestamp")
    suspend fun getRecentLaunchesCount(timestamp: Long): Int
}