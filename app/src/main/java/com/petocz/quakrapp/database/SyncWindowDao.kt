package com.petocz.quakrapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SyncWindowDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSyncWindow(syncWindow: SyncWindowEntity)

    @Query("SELECT * FROM sync_windows ORDER BY startTime ASC")
    suspend fun getAllSyncWindows(): List<SyncWindowEntity>

    @Query(
        """
            SELECT * FROM sync_windows
            WHERE startTime = :startTime
            AND endTime = :endTime
            LIMIT 1
        """
    )
    suspend fun getSyncWindow(
        startTime: Long,
        endTime: Long
    ): SyncWindowEntity?


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


    @Query("DELETE FROM sync_windows")
    suspend fun deleteAllSyncWindows()
}