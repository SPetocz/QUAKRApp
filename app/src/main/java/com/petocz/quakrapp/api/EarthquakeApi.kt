package com.petocz.quakrapp.api

import com.petocz.quakrapp.models.EarthquakeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class EarthquakeApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getEarthquakes(
        startTime: String,
        endTime: String,
        minMagnitude: Double = 1.0,
    ): EarthquakeResponse {
        return client.get(
            "https://earthquake.usgs.gov/fdsnws/event/1/query" +
                    "?format=geojson" +
                    "&starttime=$startTime" +
                    "&endtime=$endTime" +
                    "&minmagnitude=$minMagnitude"
        ).body()
    }
}