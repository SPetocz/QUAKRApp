package com.petocz.quakrapp.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: EarthquakeDatabase? = null

    fun getDatabase(context: Context): EarthquakeDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                EarthquakeDatabase::class.java,
                "earthquake_database"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}