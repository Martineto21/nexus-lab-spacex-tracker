package com.nexuslab.spacextracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Launchpad(
    @SerialName("id")
    val id: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("full_name")
    val fullName: String,
    
    @SerialName("locality")
    val locality: String,
    
    @SerialName("region")
    val region: String,
    
    @SerialName("timezone")
    val timezone: String,
    
    @SerialName("latitude")
    val latitude: Double,
    
    @SerialName("longitude")
    val longitude: Double,
    
    @SerialName("launch_attempts")
    val launchAttempts: Int,
    
    @SerialName("launch_successes")
    val launchSuccesses: Int,
    
    @SerialName("rockets")
    val rockets: List<String>,
    
    @SerialName("status")
    val status: String,
    
    @SerialName("details")
    val details: String?
) {
    val successRate: Float
        get() = if (launchAttempts > 0) {
            (launchSuccesses.toFloat() / launchAttempts.toFloat()) * 100f
        } else {
            0f
        }
}