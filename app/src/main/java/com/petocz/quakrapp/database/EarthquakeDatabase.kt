package com.petocz.quakrapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EarthquakeEntity::class,
        SyncWindowEntity::class],
    version = 2,
    exportSchema = false
)
abstract class EarthquakeDatabase : RoomDatabase() {

    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun syncWindowDao(): SyncWindowDao
}