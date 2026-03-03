package com.nexuslab.spacextracker.presentation

import com.nexuslab.spacextracker.data.model.Launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun isFutureLaunch(dateUtc: String, now: Instant = Clock.System.now()): Boolean {
    return try {
        Instant.parse(dateUtc) > now
    } catch (_: Exception) {
        false
    }
}

fun selectUpcomingLaunches(launches: List<Launch>, now: Instant = Clock.System.now()): List<Launch> {
    val strictlyFuture = launches.filter { isFutureLaunch(it.dateUtc, now) }
    if (strictlyFuture.isNotEmpty()) return strictlyFuture

    // Fallback for inconsistent API payloads where `upcoming=true` is set on past dates.
    return launches.filter { it.upcoming }
}

fun selectNextUpcomingLaunch(launches: List<Launch>, now: Instant = Clock.System.now()): Launch? {
    val nextStrictFuture = launches
        .mapNotNull { launch -> parseInstant(launch.dateUtc)?.let { instant -> instant to launch } }
        .filter { (instant, _) -> instant > now }
        .minByOrNull { (instant, _) -> instant }
        ?.second
    if (nextStrictFuture != null) return nextStrictFuture

    // Fallback for inconsistent payloads without future dates.
    return launches
        .filter { it.upcoming }
        .map { launch -> parseInstant(launch.dateUtc) to launch }
        .sortedWith(compareBy<Pair<Instant?, Launch>> { it.first == null }.thenBy { it.first })
        .firstOrNull()
        ?.second
}

private fun parseInstant(dateUtc: String): Instant? {
    return try {
        Instant.parse(dateUtc)
    } catch (_: Exception) {
        null
    }
}
