package com.nexuslab.spacextracker.presentation

import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Links
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LaunchFiltersTest {

    private fun launch(
        id: String,
        dateUtc: String,
        upcoming: Boolean
    ): Launch {
        return Launch(
            id = id,
            name = id,
            dateUtc = dateUtc,
            success = null,
            upcoming = upcoming,
            details = null,
            links = Links(),
            rocketId = "rocket",
            launchpadId = "launchpad"
        )
    }

    @Test
    fun `selectUpcomingLaunches uses strict future dates when available`() {
        val now = Instant.parse("2026-03-02T00:00:00Z")
        val launches = listOf(
            launch("past-upcoming", "2022-12-05T00:00:00Z", upcoming = true),
            launch("future", "2026-12-01T00:00:00Z", upcoming = false)
        )

        val result = selectUpcomingLaunches(launches, now)

        assertEquals(1, result.size)
        assertEquals("future", result.first().id)
    }

    @Test
    fun `selectUpcomingLaunches falls back to upcoming flag when no future dates exist`() {
        val now = Instant.parse("2026-03-02T00:00:00Z")
        val launches = listOf(
            launch("legacy-upcoming", "2022-12-05T00:00:00Z", upcoming = true),
            launch("past", "2022-01-01T00:00:00Z", upcoming = false)
        )

        val result = selectUpcomingLaunches(launches, now)

        assertEquals(1, result.size)
        assertEquals("legacy-upcoming", result.first().id)
    }

    @Test
    fun `isFutureLaunch handles invalid date safely`() {
        assertFalse(isFutureLaunch("invalid-date", Instant.parse("2026-03-02T00:00:00Z")))
        assertTrue(isFutureLaunch("2026-03-03T00:00:00Z", Instant.parse("2026-03-02T00:00:00Z")))
    }

    @Test
    fun `selectNextUpcomingLaunch returns nearest strict future launch`() {
        val now = Instant.parse("2026-03-02T00:00:00Z")
        val launches = listOf(
            launch("future-2", "2026-04-01T00:00:00Z", upcoming = true),
            launch("future-1", "2026-03-10T00:00:00Z", upcoming = false),
            launch("past-upcoming", "2022-12-05T00:00:00Z", upcoming = true)
        )

        val result = selectNextUpcomingLaunch(launches, now)

        assertEquals("future-1", result?.id)
    }

    @Test
    fun `selectNextUpcomingLaunch falls back to upcoming when no future dates exist`() {
        val now = Instant.parse("2026-03-02T00:00:00Z")
        val launches = listOf(
            launch("legacy-1", "2022-12-05T00:00:00Z", upcoming = true),
            launch("legacy-2", "2022-12-10T00:00:00Z", upcoming = true),
            launch("past", "2022-01-01T00:00:00Z", upcoming = false)
        )

        val result = selectNextUpcomingLaunch(launches, now)

        assertEquals("legacy-1", result?.id)
    }
}
