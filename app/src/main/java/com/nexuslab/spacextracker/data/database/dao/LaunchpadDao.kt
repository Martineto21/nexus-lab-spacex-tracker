package com.nexuslab.spacextracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nexuslab.spacextracker.data.database.entity.LaunchpadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchpadDao {
    
    @Query("SELECT * FROM launchpads ORDER BY name")
    fun getAllLaunchpads(): Flow<List<LaunchpadEntity>>
    
    @Query("SELECT * FROM launchpads WHERE status = 'active' ORDER BY name")
    fun getActiveLaunchpads(): Flow<List<LaunchpadEntity>>
    
    @Query("SELECT * FROM launchpads WHERE id = :id")
    suspend fun getLaunchpadById(id: String): LaunchpadEntity?
    
    @Query("SELECT * FROM launchpads WHERE id = :id")
    fun getLaunchpadByIdFlow(id: String): Flow<LaunchpadEntity?>
    
    @Query("SELECT * FROM launchpads WHERE name LIKE '%' || :query || '%' ORDER BY name")
    fun searchLaunchpads(query: String): Flow<List<LaunchpadEntity>>
    
    @Query("SELECT COUNT(*) FROM launchpads WHERE status = 'active'")
    suspend fun getActiveLaunchpadsCount(): Int
    
    @Query("SELECT COUNT(*) FROM launchpads")
    suspend fun getTotalLaunchpadsCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunchpad(launchpad: LaunchpadEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunchpads(launchpads: List<LaunchpadEntity>)
    
    @Update
    suspend fun updateLaunchpad(launchpad: LaunchpadEntity)
    
    @Delete
    suspend fun deleteLaunchpad(launchpad: LaunchpadEntity)
    
    @Query("DELETE FROM launchpads")
    suspend fun deleteAllLaunchpads()
    
    @Query("DELETE FROM launchpads WHERE lastUpdated < :timestamp")
    suspend fun deleteOldLaunchpads(timestamp: Long)
}