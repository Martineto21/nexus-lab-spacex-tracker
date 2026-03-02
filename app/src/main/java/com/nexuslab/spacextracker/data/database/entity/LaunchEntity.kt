package com.nexuslab.spacextracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexuslab.spacextracker.data.model.Launch
import kotlinx.datetime.Instant

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
            flightNumber = flightNumber,
            dateUtc = Instant.parse(dateUtc),
            success = success,
            upcoming = upcoming,
            rocket = rocketId,
            launchpad = launchpadId,
            details = details,
            links = null, // Would need JSON parsing
            cores = emptyList(), // Would need JSON parsing
            payloads = emptyList(), // Would need JSON parsing
            fairings = null, // Would need JSON parsing
            staticFireDateUtc = staticFireDateUtc?.let { Instant.parse(it) },
            netPrecision = netPrecision,
            window = if (windowStart != null && windowEnd != null) {
                Instant.parse(windowStart) to Instant.parse(windowEnd)
            } else null,
            tbd = tbd,
            tnet = tnet,
            holdReason = holdReason,
            failureReason = failureReason
        )
    }
    
    companion object {
        fun fromDomain(launch: Launch): LaunchEntity {
            return LaunchEntity(
                id = launch.id,
                name = launch.name,
                flightNumber = launch.flightNumber,
                dateUtc = launch.dateUtc.toString(),
                success = launch.success,
                upcoming = launch.upcoming,
                rocketId = launch.rocket,
                launchpadId = launch.launchpad,
                details = launch.details,
                links = null, // Would need JSON serialization
                cores = null, // Would need JSON serialization  
                payloads = null, // Would need JSON serialization
                fairings = null, // Would need JSON serialization
                staticFireDateUtc = launch.staticFireDateUtc?.toString(),
                netPrecision = launch.netPrecision,
                windowStart = launch.window?.first?.toString(),
                windowEnd = launch.window?.second?.toString(),
                tbd = launch.tbd,
                tnet = launch.tnet,
                holdReason = launch.holdReason,
                failureReason = launch.failureReason
            )
        }
    }
}