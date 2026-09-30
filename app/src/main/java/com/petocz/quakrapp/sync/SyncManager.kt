package com.petocz.quakrapp.sync

import com.petocz.quakrapp.api.EarthquakeRepository
import com.petocz.quakrapp.database.SyncWindowDao
import com.petocz.quakrapp.database.SyncWindowEntity
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SyncManager(
    private val repository: EarthquakeRepository,
    private val syncWindowDao: SyncWindowDao
) {

    private val initialSyncStart = Instant.parse(
        "2026-09-28T00:00:00Z"
    )

    private data class SyncWindow(
        val startTime: Long,
        val endTime: Long
    )

    suspend fun syncMissingWindows() {

        println("QUAKR SYNC: Starting sync check")

        val currentHourStart = Instant.now()
            .truncatedTo(ChronoUnit.HOURS)

        println("QUAKR SYNC: Start boundary = $initialSyncStart")
        println("QUAKR SYNC: Current hour = $currentHourStart")

        val missingWindows = mutableListOf<SyncWindow>()

        var windowStart = initialSyncStart

        while (windowStart.isBefore(currentHourStart)) {

            val windowEnd = windowStart.plus(
                1,
                ChronoUnit.HOURS
            )

            val startTime = windowStart.toEpochMilli()
            val endTime = windowEnd.toEpochMilli()

            if (!hasSyncWindow(startTime, endTime)) {

                println(
                    "QUAKR SYNC: Missing window " +
                            "$windowStart → $windowEnd"
                )

                missingWindows.add(
                    SyncWindow(
                        startTime = startTime,
                        endTime = endTime
                    )
                )

            } else {

                println(
                    "QUAKR SYNC: Already synced " +
                            "$windowStart → $windowEnd"
                )
            }

            windowStart = windowEnd
        }

        println(
            "QUAKR SYNC: Found ${missingWindows.size} missing hours"
        )

        val continuousGaps = findContinuousGaps(missingWindows)

        println(
            "QUAKR SYNC: Found ${continuousGaps.size} continuous gaps"
        )

        for ((index, gap) in continuousGaps.withIndex()) {

            val gapStart = Instant.ofEpochMilli(
                gap.first().startTime
            )

            val gapEnd = Instant.ofEpochMilli(
                gap.last().endTime
            )

            println(
                "QUAKR SYNC: Gap ${index + 1} = " +
                        "$gapStart → $gapEnd " +
                        "(${gap.size} hours)"
            )

            syncGap(
                startTime = gap.first().startTime,
                endTime = gap.last().endTime
            )
        }

        println("QUAKR SYNC: Sync check complete")
    }

    private fun findContinuousGaps(
        missingWindows: List<SyncWindow>
    ): List<List<SyncWindow>> {

        if (missingWindows.isEmpty()) {
            return emptyList()
        }

        val gaps = mutableListOf<MutableList<SyncWindow>>()

        var currentGap = mutableListOf(missingWindows.first())

        for (i in 1 until missingWindows.size) {

            val previousWindow = missingWindows[i - 1]
            val currentWindow = missingWindows[i]

            if (previousWindow.endTime == currentWindow.startTime) {

                currentGap.add(currentWindow)

            } else {

                gaps.add(currentGap)

                currentGap = mutableListOf(currentWindow)
            }
        }

        gaps.add(currentGap)

        return gaps
    }

    private suspend fun syncGap(
        startTime: Long,
        endTime: Long
    ) {

        val startInstant = Instant.ofEpochMilli(startTime)
        val endInstant = Instant.ofEpochMilli(endTime)

        println(
            "QUAKR SYNC: Fetching USGS data " +
                    "$startInstant → $endInstant"
        )

        val startIso = DateTimeFormatter.ISO_INSTANT.format(
            startInstant
        )

        val endIso = DateTimeFormatter.ISO_INSTANT.format(
            endInstant
        )

        repository.fetchAndStoreEarthquakes(
            startTime = startIso,
            endTime = endIso,
            minMagnitude = 1.0
        )

        println(
            "QUAKR SYNC: USGS data stored successfully"
        )

        var windowStart = startInstant

        while (windowStart.isBefore(endInstant)) {

            val windowEnd = windowStart.plus(
                1,
                ChronoUnit.HOURS
            )

            syncWindowDao.insertSyncWindow(
                SyncWindowEntity(
                    startTime = windowStart.toEpochMilli(),
                    endTime = windowEnd.toEpochMilli(),
                    completedTime = System.currentTimeMillis()
                )
            )

            println(
                "QUAKR SYNC: Marked complete " +
                        "$windowStart → $windowEnd"
            )

            windowStart = windowEnd
        }
    }

    private suspend fun hasSyncWindow(
        startTime: Long,
        endTime: Long
    ): Boolean {

        return syncWindowDao.getSyncWindow(
            startTime = startTime,
            endTime = endTime
        ) != null
    }
}