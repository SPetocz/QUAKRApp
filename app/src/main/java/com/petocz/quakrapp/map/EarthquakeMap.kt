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
    onMapReady: (MapLibreMap) -> Unit
) {
    var mapLibreMap = remember { mutableStateOf<MapLibreMap?>(null) }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->
                    map.setStyle(MAP_STYLE) {
                        mapLibreMap.value = map
                        map.cameraPosition = DEFAULT_CAMERA

                        map.uiSettings.apply {
                            isRotateGesturesEnabled = false
                            isTiltGesturesEnabled = false
                            isZoomGesturesEnabled = true
                            isScrollGesturesEnabled = true
                        }

                        map.setMinZoomPreference(1.0)
                        map.setMaxZoomPreference(12.0)

                        val features = earthquakes.map { earthquake ->
                            Feature.fromGeometry(
                                Point.fromLngLat(
                                    earthquake.longitude,
                                    earthquake.latitude
                                )
                            )
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

                        onMapReady(map)
                    }
                }
            }
        }
    )
    LaunchedEffect(earthquakes) {
        val map = mapLibreMap.value ?: return@LaunchedEffect
        val source = map.style?.getSourceAs<GeoJsonSource>("earthquakes-source")
            ?: return@LaunchedEffect

        val features = earthquakes.map { earthquake ->
            Feature.fromGeometry(
                Point.fromLngLat(
                    earthquake.longitude,
                    earthquake.latitude
                )
            )
        }

        source.setGeoJson(
            FeatureCollection.fromFeatures(features)
        )
    }
}