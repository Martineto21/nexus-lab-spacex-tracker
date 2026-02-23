package com.nexuslab.spacextracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rocket(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("active")
    val active: Boolean,
    @SerialName("stages")
    val stages: Int,
    @SerialName("boosters")
    val boosters: Int,
    @SerialName("cost_per_launch")
    val costPerLaunch: Int,
    @SerialName("success_rate_pct")
    val successRatePct: Int,
    @SerialName("first_flight")
    val firstFlight: String,
    @SerialName("country")
    val country: String,
    @SerialName("company")
    val company: String,
    @SerialName("description")
    val description: String,
    @SerialName("wikipedia")
    val wikipedia: String,
    @SerialName("flickr_images")
    val flickrImages: List<String>
)