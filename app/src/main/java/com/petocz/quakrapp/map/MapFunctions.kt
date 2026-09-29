package com.petocz.quakrapp.map
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.*

fun resetMap(map: MapLibreMap){
    map.animateCamera(
        CameraUpdateFactory.newCameraPosition(DEFAULT_CAMERA),
        800
    )
}
