package com.nexuslab.spacextracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Launch(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("date_utc")
    val dateUtc: String,
    @SerialName("success")
    val success: Boolean?,
    @SerialName("upcoming")
    val upcoming: Boolean,
    @SerialName("details")
    val details: String?,
    @SerialName("links")
    val links: Links,
    @SerialName("rocket")
    val rocketId: String,
    @SerialName("launchpad")
    val launchpadId: String
)

@Serializable
data class Links(
    @SerialName("patch")
    val patch: Patch?,
    @SerialName("reddit")
    val reddit: Reddit?,
    @SerialName("article")
    val article: String?,
    @SerialName("wikipedia")
    val wikipedia: String?,
    @SerialName("video")
    val video: String?
)

@Serializable
data class Patch(
    @SerialName("small")
    val small: String?,
    @SerialName("large")
    val large: String?
)

@Serializable
data class Reddit(
    @SerialName("campaign")
    val campaign: String?,
    @SerialName("launch")
    val launch: String?,
    @SerialName("media")
    val media: String?,
    @SerialName("recovery")
    val recovery: String?
)