package com.nexuslab.spacextracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nexuslab.spacextracker.data.database.entity.RocketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RocketDao {
    
    @Query("SELECT * FROM rockets ORDER BY name")
    fun getAllRockets(): Flow<List<RocketEntity>>
    
    @Query("SELECT * FROM rockets WHERE active = 1 ORDER BY name")
    fun getActiveRockets(): Flow<List<RocketEntity>>
    
    @Query("SELECT * FROM rockets WHERE id = :id")
    suspend fun getRocketById(id: String): RocketEntity?
    
    @Query("SELECT * FROM rockets WHERE id = :id")
    fun getRocketByIdFlow(id: String): Flow<RocketEntity?>
    
    @Query("SELECT * FROM rockets WHERE name LIKE '%' || :query || '%' ORDER BY name")
    fun searchRockets(query: String): Flow<List<RocketEntity>>
    
    @Query("SELECT COUNT(*) FROM rockets WHERE active = 1")
    suspend fun getActiveRocketsCount(): Int
    
    @Query("SELECT COUNT(*) FROM rockets")
    suspend fun getTotalRocketsCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRocket(rocket: RocketEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRockets(rockets: List<RocketEntity>)
    
    @Update
    suspend fun updateRocket(rocket: RocketEntity)
    
    @Delete
    suspend fun deleteRocket(rocket: RocketEntity)
    
    @Query("DELETE FROM rockets")
    suspend fun deleteAllRockets()
    
    @Query("DELETE FROM rockets WHERE lastUpdated < :timestamp")
    suspend fun deleteOldRockets(timestamp: Long)
}