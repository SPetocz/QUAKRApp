package com.petocz.quakrapp.map
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.*

fun resetMap(map: MapLibreMap){
    map.animateCamera(
        CameraUpdateFactory.newCameraPosition(DEFAULT_CAMERA),
        800
    )

}

fun resetMap(
    map: MapLibreMap,
    latitude: Double,
    longitude: Double
) {
    val cameraPosition = org.maplibre.android.camera.CameraPosition.Builder()
        .target(LatLng(latitude, longitude))
        .zoom(8.0)
        .bearing(0.0)
        .tilt(0.0)
        .build()

    map.animateCamera(
        CameraUpdateFactory.newCameraPosition(cameraPosition),
        800
    )
}
