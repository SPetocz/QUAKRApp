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
import com.petocz.quakrapp.sync.SyncManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MapLibre.getInstance(this)

        enableEdgeToEdge()

        setContent {
            QUAKRAppTheme {
                MapScreen()
            }
        }
    }
}