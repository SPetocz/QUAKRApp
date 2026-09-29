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

private val DEFAULT_CAMERA = CameraPosition.Builder()
    .target(LatLng(39.0, -95.5))
    .zoom(2.2)
    .build()

@Composable

fun EarthquakeMap(){

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->
                    map.setStyle(MAP_STYLE){
                        map.cameraPosition = DEFAULT_CAMERA
                        map.uiSettings.apply {
                            isRotateGesturesEnabled = false
                            isTiltGesturesEnabled = false
                        }
                        map.setMinZoomPreference(0.5)
                        map.setMaxZoomPreference(5.0)
                    }
                }
            }
        }
    )
}