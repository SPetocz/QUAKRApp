package com.petocz.quakrapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EarthquakeDao  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarthquakes(earthquakes: List<EarthquakeEntity>)

    @Query("SELECT * FROM earthquakes ORDER BY time DESC")
    suspend fun getAllEarthquakes(): List<EarthquakeEntity>

    @Query(
        """
            SELECT * FROM earthquakes
            WHERE time BETWEEN :startTime  and :endTime
            AND magnitude >= :minMagnitude
            ORDER BY time DESC
        """
    )
    suspend fun getFilteredEarthquakes(
        startTime: Long,
        endTime: Long,
        minMagnitude: Double
    ): List<EarthquakeEntity>

    @Query(
        """
    SELECT * FROM earthquakes
    WHERE time >= :startTime
    AND time < :endTime
    ORDER BY time DESC
    """
    )
    suspend fun getEarthquakesForTimeRange(
        startTime: Long,
        endTime: Long
    ): List<EarthquakeEntity>

    @Query("DELETE FROM earthquakes")
    suspend fun deleteAllEarthquakes()
}