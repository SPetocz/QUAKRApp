package com.petocz.quakrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.petocz.quakrapp.ui.theme.QUAKRAppTheme
import com.petocz.quakrapp.views.MapScreen
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MapLibre.getInstance(this)

        enableEdgeToEdge()


        val application = application as QuakrApplication

        lifecycleScope.launch {
            application.earthquakeRepository.fetchAndStoreEarthquakes(
                startTime = "2026-09-28T00:00:00",
                endTime = "2026-09-28T23:59:59",
                minMagnitude = 1.0
            )

            val earthquakes =
                application.earthquakeRepository.getAllEarthquakes()

            println("QUAKR: ${earthquakes.size} earthquakes stored")
        }

        setContent {
            QUAKRAppTheme {
                MapScreen()
            }
        }
    }
}