package com.petocz.quakrapp.models

import androidx.compose.ui.layout.LayoutCoordinates
import kotlinx.serialization.Serializable
import org.maplibre.geojson.Geometry

@Serializable
data class EarthquakeResponse(
    val type: String,
    val features : List<Earthquake>
)

@Serializable
data class Earthquake(
    val id: String,
    val properties: EarthquakeProperties,
    val geometry: EarthquakeGeometry
)

@Serializable
data class EarthquakeProperties(
    val mag: Double?,
    val place: String?,
    val time: Long?
)

@Serializable
data class EarthquakeGeometry(
    val coordinates: List<Double> = emptyList()
)