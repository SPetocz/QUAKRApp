package com.petocz.quakrapp.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private const val PREFS_NAME = "quakr_location_cache"
private const val KEY_LATITUDE = "latitude"
private const val KEY_LONGITUDE = "longitude"
private const val KEY_TIMESTAMP = "timestamp"

// Refresh the cached location every 5 minutes.
private const val CACHE_DURATION_MS = 5 * 60 * 1000L

fun getCachedLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val finePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarsePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!finePermission && !coarsePermission) {
        onError("Location permission has not been granted.")
        return
    }

    val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    val cachedLatitude = prefs.getFloat(KEY_LATITUDE, Float.NaN)
    val cachedLongitude = prefs.getFloat(KEY_LONGITUDE, Float.NaN)
    val cachedTimestamp = prefs.getLong(KEY_TIMESTAMP, 0L)

    val hasCache = !cachedLatitude.isNaN() &&
            !cachedLongitude.isNaN()

    val cacheIsFresh = hasCache &&
            System.currentTimeMillis() - cachedTimestamp < CACHE_DURATION_MS

    // Immediately use the cached location if one exists.
    if (hasCache) {
        onLocationReceived(
            cachedLatitude.toDouble(),
            cachedLongitude.toDouble()
        )
    }

    // Skip the location request if the cache is still fresh.
    if (cacheIsFresh) return

    val client = LocationServices.getFusedLocationProviderClient(context)

    try {
        client.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {
                // Save the fresh coordinates for the next button press.
                prefs.edit()
                    .putFloat(KEY_LATITUDE, location.latitude.toFloat())
                    .putFloat(KEY_LONGITUDE, location.longitude.toFloat())
                    .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                    .apply()

                // Only move the map here if no cached location existed.
                if (!hasCache) {
                    onLocationReceived(
                        location.latitude,
                        location.longitude
                    )
                }
            } else if (!hasCache) {
                onError("Unable to determine your location. Try again.")
            }

        }.addOnFailureListener {
            if (!hasCache) {
                onError("Could not retrieve your location.")
            }
        }
    } catch (e: SecurityException) {
        if (!hasCache) {
            onError("Location permission is required.")
        }
    }
}