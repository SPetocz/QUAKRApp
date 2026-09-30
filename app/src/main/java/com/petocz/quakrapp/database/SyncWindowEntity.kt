package com.petocz.quakrapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sync_windows",
    indices = [
        Index(
            value = ["startTime", "endTime"],
            unique = true
        )
    ])
data class SyncWindowEntity(
    @PrimaryKey(autoGenerate = true)
    val syncId: Int = 0,

    val startTime: Long,
    val endTime: Long,
    val completedTime: Long
)