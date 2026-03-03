package com.nexuslab.spacextracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexuslab.spacextracker.data.model.Rocket

@Entity(tableName = "rockets")
data class RocketEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val active: Boolean,
    val stages: Int,
    val boosters: Int,
    val costPerLaunch: Int,
    val successRatePct: Int,
    val firstFlight: String, // Date as string
    val country: String,
    val company: String,
    val wikipedia: String?,
    val description: String?,
    val heightMeters: Double,
    val diameterMeters: Double,
    val massKg: Int,
    val flickrImages: String, // JSON array as string
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Rocket {
        return Rocket(
            id = id,
            name = name,
            type = type,
            active = active,
            stages = stages,
            boosters = boosters,
            costPerLaunch = costPerLaunch,
            successRatePct = successRatePct,
            firstFlight = firstFlight,
            country = country,
            company = company,
            wikipedia = wikipedia ?: "",
            description = description ?: "",
            flickrImages = emptyList() // Would need JSON parsing
        )
    }
    
    companion object {
        fun fromDomain(rocket: Rocket): RocketEntity {
            return RocketEntity(
                id = rocket.id,
                name = rocket.name,
                type = rocket.type,
                active = rocket.active,
                stages = rocket.stages,
                boosters = rocket.boosters,
                costPerLaunch = rocket.costPerLaunch,
                successRatePct = rocket.successRatePct,
                firstFlight = rocket.firstFlight,
                country = rocket.country,
                company = rocket.company,
                wikipedia = rocket.wikipedia,
                description = rocket.description,
                heightMeters = 0.0,
                diameterMeters = 0.0,
                massKg = 0,
                flickrImages = "" // Would need JSON serialization
            )
        }
    }
}
