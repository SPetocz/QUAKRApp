package com.petocz.quakrapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.petocz.quakrapp.api.EarthquakeRepository
import com.petocz.quakrapp.database.EarthquakeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: EarthquakeRepository
) : ViewModel() {
    private val _earthquakes = MutableStateFlow<List<EarthquakeEntity>>(emptyList())
    val earthquakes: StateFlow<List<EarthquakeEntity>> = _earthquakes

    fun loadEarthquakes() {
        viewModelScope.launch {

            val startOfDay = java.time.LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val startOfTomorrow = java.time.LocalDate.now()
                .plusDays(1)
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            _earthquakes.value = repository.getEarthquakesForTimeRange(
                startTime = startOfDay,
                endTime = startOfTomorrow
            )
        }
    }

    fun loadFilteredEarthquakes(
        startTime: Long,
        endTime: Long,
        minMagnitude: Double
    ){
        viewModelScope.launch {
            _earthquakes.value = repository.getFilteredEarthquakes(
                startTime = startTime,
                endTime = endTime,
                minMagnitude = minMagnitude
                    )
        }

    }
}

class MapViewModelFactory(
    private val repository: EarthquakeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}