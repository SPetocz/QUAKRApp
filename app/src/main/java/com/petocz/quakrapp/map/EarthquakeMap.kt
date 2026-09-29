package com.petocz.quakrapp.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.camera.CameraUpdateFactory

private const val MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

val DEFAULT_CAMERA = CameraPosition.Builder()
    .target(LatLng(39.0, -95.5))
    .zoom(2.2)
    .bearing(0.0)
    .tilt(0.0)
    .build()

@Composable

fun EarthquakeMap(
    onMapReady: (MapLibreMap) -> Unit
){

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->
                    map.setStyle(MAP_STYLE){
                        map.cameraPosition = DEFAULT_CAMERA
                        map.uiSettings.apply {
                            isRotateGesturesEnabled = false
                            isTiltGesturesEnabled = false
                            isZoomGesturesEnabled = true
                            isScrollGesturesEnabled = true
                        }
                        map.setMinZoomPreference(1.0)
                        map.setMaxZoomPreference(12.0)

                        onMapReady(map)
                    }

                }
            }
        }
    )
}