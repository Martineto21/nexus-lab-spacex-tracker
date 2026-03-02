package com.nexuslab.spacextracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.nexuslab.spacextracker.data.database.dao.LaunchDao
import com.nexuslab.spacextracker.data.database.dao.LaunchpadDao
import com.nexuslab.spacextracker.data.database.dao.RocketDao
import com.nexuslab.spacextracker.data.database.entity.LaunchEntity
import com.nexuslab.spacextracker.data.database.entity.LaunchpadEntity
import com.nexuslab.spacextracker.data.database.entity.RocketEntity

@Database(
    entities = [
        LaunchEntity::class,
        RocketEntity::class,
        LaunchpadEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpaceXDatabase : RoomDatabase() {
    
    abstract fun launchDao(): LaunchDao
    abstract fun rocketDao(): RocketDao  
    abstract fun launchpadDao(): LaunchpadDao
    
    companion object {
        const val DATABASE_NAME = "spacex_database"
        
        @Volatile
        private var INSTANCE: SpaceXDatabase? = null
        
        fun getInstance(context: Context): SpaceXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpaceXDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}