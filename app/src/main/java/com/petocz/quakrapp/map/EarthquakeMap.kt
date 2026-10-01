package com.petocz.quakrapp.map

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.viewinterop.AndroidView
import com.petocz.quakrapp.database.EarthquakeEntity
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.math.cos
import kotlin.math.sin

private const val MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

val DEFAULT_CAMERA = CameraPosition.Builder()
    .target(LatLng(39.0, -95.5))
    .zoom(1.9)
    .bearing(0.0)
    .tilt(0.0)
    .build()

/*
 * Visualization radius in kilometers.
 *
 * These are visual values for QUAKR.
 * They are NOT intended to represent the actual
 * felt or damage radius of an earthquake.
 */
private fun getCircleRadius(magnitude: Double?): Double {
    val mag = magnitude ?: 1.0

    return when {
        mag >= 8.0 -> 2000.0
        mag >= 7.0 -> 1250.0
        mag >= 6.0 -> 800.0
        mag >= 5.0 -> 500.0
        mag >= 4.0 -> 300.0
        mag >= 3.0 -> 175.0
        mag >= 2.0 -> 100.0
        else -> 50.0
    }
}

/*
 * Opacity based on magnitude.
 */
private fun getCircleOpacity(magnitude: Double?): Double {
    val mag = magnitude ?: 1.0

    return when {
        mag >= 8.0 -> 0.85
        mag >= 7.0 -> 0.75
        mag >= 6.0 -> 0.65
        mag >= 5.0 -> 0.55
        mag >= 4.0 -> 0.45
        mag >= 3.0 -> 0.35
        mag >= 2.0 -> 0.25
        else -> 0.15
    }
}

/*
 * Creates a geographic circle around an earthquake.
 *
 * radiusKm is a real geographic distance, so the
 * circle grows and shrinks naturally when the map
 * is zoomed.
 */
private fun createEarthquakeCircle(
    latitude: Double,
    longitude: Double,
    radiusKm: Double,
    points: Int = 64
): Polygon {

    val earthRadiusKm = 6371.0

    val coordinates = mutableListOf<Point>()

    for (i in 0..points) {

        val angle = Math.toRadians(
            i.toDouble() / points * 360.0
        )

        val latitudeOffset =
            radiusKm / earthRadiusKm * (180.0 / Math.PI)

        val longitudeOffset =
            radiusKm /
                    (earthRadiusKm * cos(Math.toRadians(latitude))) *
                    (180.0 / Math.PI)

        val circleLatitude =
            latitude + latitudeOffset * sin(angle)

        val circleLongitude =
            longitude + longitudeOffset * cos(angle)

        coordinates.add(
            Point.fromLngLat(
                circleLongitude,
                circleLatitude
            )
        )
    }

    return Polygon.fromLngLats(
        listOf(coordinates)
    )
}

private fun createEarthquakeFeature(
    earthquake: EarthquakeEntity
): Feature {

    val radius = getCircleRadius(earthquake.magnitude)
    val opacity = getCircleOpacity(earthquake.magnitude)

    return Feature.fromGeometry(
        createEarthquakeCircle(
            latitude = earthquake.latitude,
            longitude = earthquake.longitude,
            radiusKm = radius
        )
    ).apply {

        addStringProperty(
            "earthquakeId",
            earthquake.id
        )

        addNumberProperty(
            "magnitude",
            earthquake.magnitude ?: 1.0
        )

        addNumberProperty(
            "opacity",
            opacity
        )
    }
}

@Composable
fun EarthquakeMap(
    earthquakes: List<EarthquakeEntity>,
    onMapReady: (MapLibreMap) -> Unit,
    onEarthquakeClick: (EarthquakeEntity?) -> Unit
) {

    val currentEarthquakes =
        rememberUpdatedState(earthquakes)

    var mapLibreMap =
        remember {
            mutableStateOf<MapLibreMap?>(null)
        }

    AndroidView(
        factory = { context ->

            MapView(context).apply {

                getMapAsync { map ->

                    map.setStyle(MAP_STYLE) {

                        map.cameraPosition = DEFAULT_CAMERA

                        map.uiSettings.apply {
                            isRotateGesturesEnabled = false
                            isTiltGesturesEnabled = false
                            isZoomGesturesEnabled = true
                            isScrollGesturesEnabled = true
                        }

                        map.setMinZoomPreference(0.5)
                        map.setMaxZoomPreference(12.0)

                        /*
                         * Create the initial earthquake polygons.
                         */
                        val features =
                            earthquakes.map {
                                createEarthquakeFeature(it)
                            }

                        val featureCollection =
                            FeatureCollection.fromFeatures(features)

                        map.style?.addSource(
                            GeoJsonSource(
                                "earthquakes-source",
                                featureCollection
                            )
                        )

                        /*
                         * FillLayer is used instead of CircleLayer
                         * because these are now geographic polygons.
                         */
                        map.style?.addLayer(
                            FillLayer(
                                "earthquakes-layer",
                                "earthquakes-source"
                            ).withProperties(
                                fillColor("#FF0000"),
                                fillOpacity(
                                    org.maplibre.android.style.expressions.Expression
                                        .get("opacity")
                                )
                            )
                        )

                        /*
                         * Detect earthquake taps.
                         */
                        map.addOnMapClickListener { point ->

                            println(
                                "QUAKR: Map tapped at $point"
                            )

                            val screenPoint =
                                map.projection.toScreenLocation(point)

                            val features =
                                map.queryRenderedFeatures(
                                    PointF(
                                        screenPoint.x,
                                        screenPoint.y
                                    ),
                                    "earthquakes-layer"
                                )

                            println(
                                "QUAKR: Found ${features.size} features"
                            )

                            if (features.isNotEmpty()) {

                                val feature =
                                    features[0]

                                val earthquakeId =
                                    feature.getStringProperty(
                                        "earthquakeId"
                                    )

                                val earthquake =
                                    currentEarthquakes.value.find {
                                        it.id == earthquakeId
                                    }

                                println(
                                    "QUAKR: Earthquake tapped!"
                                )

                                println(
                                    "QUAKR: ID = $earthquakeId"
                                )

                                println(
                                    "QUAKR: Earthquakes in list = " +
                                            "${currentEarthquakes.value.size}"
                                )

                                println(
                                    "QUAKR: Found earthquake = " +
                                            "${earthquake != null}"
                                )

                                if (earthquake != null) {

                                    println(
                                        "QUAKR: Magnitude = " +
                                                "${earthquake.magnitude}"
                                    )

                                    println(
                                        "QUAKR: Location = " +
                                                "${earthquake.place}"
                                    )

                                    println(
                                        "QUAKR: Depth = " +
                                                "${earthquake.depth}"
                                    )

                                    println(
                                        "QUAKR: Time = " +
                                                "${earthquake.time}"
                                    )

                                    onEarthquakeClick(
                                        earthquake
                                    )
                                }

                                true

                            } else {

                                onEarthquakeClick(null)

                                false
                            }
                        }

                        mapLibreMap.value = map

                        onMapReady(map)
                    }
                }
            }
        }
    )

    /*
     * Update earthquake polygons whenever the
     * earthquake list changes.
     */
    LaunchedEffect(
        earthquakes,
        mapLibreMap.value
    ) {

        val map =
            mapLibreMap.value
                ?: return@LaunchedEffect

        val source =
            map.style?.getSourceAs<GeoJsonSource>(
                "earthquakes-source"
            ) ?: return@LaunchedEffect

        val features =
            earthquakes.map {
                createEarthquakeFeature(it)
            }

        source.setGeoJson(
            FeatureCollection.fromFeatures(features)
        )
    }
}