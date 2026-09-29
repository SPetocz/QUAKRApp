package com.petocz.quakrapp

import android.app.Application
import com.petocz.quakrapp.api.EarthquakeApi
import com.petocz.quakrapp.api.EarthquakeRepository
import com.petocz.quakrapp.database.DatabaseProvider

class QuakrApplication: Application() {

    val database by lazy {
        DatabaseProvider.getDatabase(this)
    }
    val earthquakeApi by lazy {
        EarthquakeApi()
    }
    val EarthquakeRepository by lazy {
        EarthquakeRepository(
            api = earthquakeApi,
            dao = database.EarthquakeDao()
        )
    }
}