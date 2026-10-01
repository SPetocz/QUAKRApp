package com.petocz.quakrapp.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.petocz.quakrapp.database.EarthquakeEntity
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import android.graphics.PointF
import androidx.compose.runtime.rememberUpdatedState

private const val MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

val DEFAULT_CAMERA = CameraPosition.Builder()
    .target(LatLng(39.0, -95.5))
    .zoom(1.9)
    .bearing(0.0)
    .tilt(0.0)
    .build()

@Composable
fun EarthquakeMap(
    earthquakes: List<EarthquakeEntity>,
    onMapReady: (MapLibreMap) -> Unit,
    onEarthquakeClick: (EarthquakeEntity?) -> Unit
) {
    val currentEarthquakes = rememberUpdatedState(earthquakes)
    var mapLibreMap = remember { mutableStateOf<MapLibreMap?>(null) }
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

                        val features = earthquakes.map { earthquake ->
                            Feature.fromGeometry(
                                Point.fromLngLat(
                                    earthquake.longitude,
                                    earthquake.latitude
                                )
                            ).apply {
                                addStringProperty("earthquakeId", earthquake.id)
                            }
                        }

                        val featureCollection =
                            FeatureCollection.fromFeatures(features)

                        map.style?.addSource(
                            GeoJsonSource(
                                "earthquakes-source",
                                featureCollection
                            )
                        )

                        map.style?.addLayer(
                            CircleLayer(
                                "earthquakes-layer",
                                "earthquakes-source"
                            ).withProperties(
                                circleRadius(6f),
                                circleColor("#FF0000")
                            )
                        )

                        map.addOnMapClickListener { point ->

                            println("QUAKR: Map tapped at $point")

                            val screenPoint = map.projection.toScreenLocation(point)

                            val features = map.queryRenderedFeatures(
                                PointF(screenPoint.x, screenPoint.y),
                                "earthquakes-layer"
                            )

                            println("QUAKR: Found ${features.size} features")

                            if (features.isNotEmpty()) {
                                val feature = features[0]

                                val earthquakeId = feature.getStringProperty("earthquakeId")

                                val earthquake = currentEarthquakes.value.find {
                                    it.id == earthquakeId
                                }

                                println("QUAKR: Earthquake tapped!")
                                println("QUAKR: ID = $earthquakeId")
                                println("QUAKR: Earthquakes in list = ${earthquakes.size}")
                                println("QUAKR: Found earthquake = ${earthquake != null}")

                                if (earthquake != null) {
                                    println("QUAKR: Magnitude = ${earthquake.magnitude}")
                                    println("QUAKR: Location = ${earthquake.place}")
                                    println("QUAKR: Depth = ${earthquake.depth}")
                                    println("QUAKR: Time = ${earthquake.time}")
                                    onEarthquakeClick(earthquake)
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
    LaunchedEffect(earthquakes, mapLibreMap.value) {
        val map = mapLibreMap.value ?: return@LaunchedEffect
        val source = map.style?.getSourceAs<GeoJsonSource>("earthquakes-source")
            ?: return@LaunchedEffect

        val features = earthquakes.map { earthquake ->
            Feature.fromGeometry(
                Point.fromLngLat(
                    earthquake.longitude,
                    earthquake.latitude
                )
            ).apply {
                addStringProperty("earthquakeId", earthquake.id)
            }
        }

        source.setGeoJson(
            FeatureCollection.fromFeatures(features)
        )
    }
}