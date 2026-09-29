package com.petocz.quakrapp.api

import com.petocz.quakrapp.database.EarthquakeDao
import com.petocz.quakrapp.database.EarthquakeEntity
import com.petocz.quakrapp.models.EarthquakeResponse
import kotlin.math.log
import kotlin.math.min

class EarthquakeRepository (
    private val api: EarthquakeApi,
    private val dao: EarthquakeDao
){
    suspend fun FetchAndStoreEarthquakes(
        startTime: String,
        endTime: String,
        minMagnitude: Double = 1.0
    ){
        val response: EarthquakeResponse = api.getEarthquakes(
            startTime = startTime,
            endTime = endTime,
            minMagnitude = minMagnitude
        )

        val earthquakes = response.features.map {
            earthquake ->
            EarthquakeEntity(
                id = earthquake.id,
                magnitude = earthquake.properties.mag,
                place = earthquake.properties.place,
                time = earthquake.properties.time,
                longitude = earthquake.geometry.coordinates.getOrElse(0) {0.0},
                latitude = earthquake.geometry.coordinates.getOrElse(1) { 0.0 },
                depth = earthquake.geometry.coordinates.getOrElse(2) { 0.0 }
            )
        }
        dao.insertEarthquakes(earthquakes)
    }

    suspend fun getAllEarthquakes(): List<EarthquakeEntity>{
        return dao.getAllEarthquakes()
    }

    suspend fun getFilteredEarthquakes(
        startTime: Long,
        endTime: Long,
        minMagnitude: Double
    ): List<EarthquakeEntity> {
        return dao.getFilteredEarthquakes(
            startTime = startTime,
            endTime = endTime,
            minMagnitude = minMagnitude
        )
    }

}