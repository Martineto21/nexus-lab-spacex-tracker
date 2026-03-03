package com.nexuslab.spacextracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Links

@Entity(tableName = "launches")
data class LaunchEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val flightNumber: Int,
    val dateUtc: String,
    val success: Boolean?,
    val upcoming: Boolean,
    val rocketId: String,
    val launchpadId: String,
    val details: String?,
    val links: String?, // JSON string
    val cores: String?, // JSON string  
    val payloads: String?, // JSON string
    val fairings: String?, // JSON string
    val staticFireDateUtc: String?,
    val netPrecision: String?,
    val windowStart: String?,
    val windowEnd: String?,
    val tbd: Boolean,
    val tnet: Boolean,
    val holdReason: String?,
    val failureReason: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Launch {
        return Launch(
            id = id,
            name = name,
            dateUtc = dateUtc,
            success = success,
            upcoming = upcoming,
            rocketId = rocketId,
            launchpadId = launchpadId,
            details = details,
            links = Links(
                patch = null,
                reddit = null,
                article = null,
                wikipedia = null,
                video = null
            )
        )
    }
    
    companion object {
        fun fromDomain(launch: Launch): LaunchEntity {
            return LaunchEntity(
                id = launch.id,
                name = launch.name,
                flightNumber = 0,
                dateUtc = launch.dateUtc,
                success = launch.success,
                upcoming = launch.upcoming,
                rocketId = launch.rocketId,
                launchpadId = launch.launchpadId,
                details = launch.details,
                links = null, // Would need JSON serialization
                cores = null, // Would need JSON serialization  
                payloads = null, // Would need JSON serialization
                fairings = null, // Would need JSON serialization
                staticFireDateUtc = null,
                netPrecision = null,
                windowStart = null,
                windowEnd = null,
                tbd = false,
                tnet = false,
                holdReason = null,
                failureReason = null
            )
        }
    }
}
