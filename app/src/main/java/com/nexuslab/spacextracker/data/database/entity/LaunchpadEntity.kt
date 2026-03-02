package com.nexuslab.spacextracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexuslab.spacextracker.data.model.Launchpad

@Entity(tableName = "launchpads")
data class LaunchpadEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val fullName: String,
    val locality: String,
    val region: String,
    val timezone: String,
    val latitude: Double,
    val longitude: Double,
    val launchAttempts: Int,
    val launchSuccesses: Int,
    val rockets: String, // JSON array as string
    val launches: String, // JSON array as string
    val status: String,
    val details: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Launchpad {
        return Launchpad(
            id = id,
            name = name,
            fullName = fullName,
            locality = locality,
            region = region,
            timezone = timezone,
            latitude = latitude,
            longitude = longitude,
            launchAttempts = launchAttempts,
            launchSuccesses = launchSuccesses,
            rockets = emptyList(), // Would need JSON parsing
            status = status,
            details = details
        )
    }
    
    companion object {
        fun fromDomain(launchpad: Launchpad): LaunchpadEntity {
            return LaunchpadEntity(
                id = launchpad.id,
                name = launchpad.name,
                fullName = launchpad.fullName,
                locality = launchpad.locality,
                region = launchpad.region,
                timezone = launchpad.timezone,
                latitude = launchpad.latitude,
                longitude = launchpad.longitude,
                launchAttempts = launchpad.launchAttempts,
                launchSuccesses = launchpad.launchSuccesses,
                rockets = "", // Would need JSON serialization
                launches = "", // Would need JSON serialization
                status = launchpad.status,
                details = launchpad.details
            )
        }
    }
}
