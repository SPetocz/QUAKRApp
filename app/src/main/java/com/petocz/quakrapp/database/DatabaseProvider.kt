package com.petocz.quakrapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.serialization.descriptors.StructureKind

private val MIGRATION_1_2 = object : Migration(1,2){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sync_windows (
                syncId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER NOT NULL,
                completedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_sync_windows_startTime_endTime
            ON sync_windows(startTime, endTime)
            """.trimIndent()
        )
    }
}

object DatabaseProvider {

    @Volatile
    private var INSTANCE: EarthquakeDatabase? = null

    fun getDatabase(context: Context): EarthquakeDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                EarthquakeDatabase::class.java,
                "earthquake_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also {
                    INSTANCE = it
            }
        }
    }
}