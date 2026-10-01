package com.petocz.quakrapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petocz.quakrapp.QuakrApplication
import com.petocz.quakrapp.R
import com.petocz.quakrapp.database.EarthquakeEntity
import com.petocz.quakrapp.map.EarthquakeMap
import com.petocz.quakrapp.ui.theme.*
import com.petocz.quakrapp.map.*
import com.petocz.quakrapp.viewmodel.MapViewModel
import com.petocz.quakrapp.viewmodel.MapViewModelFactory
import org.maplibre.android.maps.MapLibreMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {

    val context = LocalContext.current
    val application = context.applicationContext as QuakrApplication
    var showFilterView by remember { mutableStateOf(false) }
    var selectedMinMagnitude: Double by remember { mutableStateOf(1.0) }

    var selectedStartDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var selectedEndDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var showStartDatePicker by remember {
        mutableStateOf(false)
    }

    var showEndDatePicker by remember {
        mutableStateOf(false)
    }

    var confirmedMinMagnitude: Double by remember { mutableStateOf(1.0) }

    var confirmedStartDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var confirmedEndDate by remember {
        mutableStateOf(LocalDate.now())
    }

    fun confirmFilters() {
        confirmedMinMagnitude = selectedMinMagnitude
        confirmedStartDate = selectedStartDate
        confirmedEndDate = selectedEndDate

        showStartDatePicker = false
        showEndDatePicker = false
        showFilterView = false

        // Eventually this is where we will apply
        // the filters to your earthquake data.
    }

    fun cancelFilterChanges() {
        selectedMinMagnitude = confirmedMinMagnitude
        selectedStartDate = confirmedStartDate
        selectedEndDate = confirmedEndDate

        showStartDatePicker = false
        showEndDatePicker = false
        showFilterView = false
    }

    fun dateToMillis(date: LocalDate): Long {
        return date
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun resetFilters() {
        selectedMinMagnitude = 1.0
        selectedStartDate = LocalDate.now()
        selectedEndDate = LocalDate.now()

        confirmedMinMagnitude = 1.0
        confirmedStartDate = LocalDate.now()
        confirmedEndDate = LocalDate.now()

        showStartDatePicker = false
        showEndDatePicker = false
        showFilterView = false
    }

    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            application.earthquakeRepository
        )
    )

    val earthquakes by viewModel.earthquakes.collectAsState()
    var selectedEarthquake by remember {
        mutableStateOf<EarthquakeEntity?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.loadEarthquakes()
    }

    var mapLibreMap by remember {
        mutableStateOf<MapLibreMap?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primary),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Text(
                text = "QUAKR",
                fontSize = 60.sp,
                fontFamily = anton,
                color = Color.White,
                letterSpacing = 6.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 4f
                    )
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        showFilterView = false
                        selectedEarthquake = null
                        resetFilters()
                        //do something
                    },
                    modifier = Modifier
                        .background(primary)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(40.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),

                    ) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "menu bar",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }
                Button(
                    onClick = {
                        mapLibreMap?.let { resetMap(it) }
                        selectedEarthquake = null
                        showFilterView = false
                        resetFilters()

                    },
                    modifier = Modifier
                        .background(primary)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(40.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "search icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }
                Button(
                    onClick = {
                        showFilterView = true
                        selectedEarthquake = null
                    },
                    modifier = Modifier
                        .background(primary)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(40.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = "filter icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }
                Button(
                    onClick = {
                        mapLibreMap?.let { resetMap(it) }
                        selectedEarthquake = null
                        showFilterView = false
                        viewModel.loadEarthquakes()
                        resetFilters()

                    },
                    modifier = Modifier
                        .background(primary)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(40.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.reset),
                        contentDescription = "reset map icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
            Text(
                text = "EARTHQUAKES: ${earthquakes.size}",
                fontFamily = anton,
                fontSize = 28.sp,
                letterSpacing = 2.sp,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 4f
                    )
                )
            )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
                    .clip(
                        RoundedCornerShape(10.dp))
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                EarthquakeMap(
                    earthquakes = earthquakes,
                    onMapReady = { map ->
                        mapLibreMap = map
                    },
                    onEarthquakeClick = { earthquake ->
                        println("QUAKR: Selected earthquake = ${earthquake?.id}")
                        selectedEarthquake = earthquake
                        showFilterView = false
                    }
                )
                selectedEarthquake?.let { earthquake ->

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(20.dp)
                            .shadow(
                                elevation = 0.dp,
                            shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Column {
                            Text(
                                text = "Earthquake",
                                fontSize = 24.sp,
                                fontFamily = anton,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Magnitude: ${earthquake.magnitude ?: "Unknown"}",
                                fontSize = 16.sp,
                                color = Color.Black
                            )

                            Text(
                                text = "Location: ${earthquake.place ?: "Unknown"}",
                                fontSize = 16.sp,
                                color = Color.Black
                            )

                            Text(
                                text = "Time: ${
                                    earthquake.time?.let {
                                        java.text.SimpleDateFormat(
                                            "MMM d, yyyy h:mm a",
                                            java.util.Locale.getDefault()
                                        ).format(java.util.Date(it))
                                    } ?: "Unknown"
                                }",
                                fontSize = 16.sp,
                                color = Color.Black
                            )

                            Text(
                                text = "Depth: %.2f km".format(earthquake.depth),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }

                }
                if(showFilterView){
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = "FILTER",
                                textAlign = TextAlign.Center,
                                fontFamily = anton,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Minimum Magnitude:",
                                        fontSize = 16.sp,
                                        fontFamily = anton,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "${"   %.1f".format(selectedMinMagnitude)}+",
                                        fontSize = 16.sp,
                                        color = primary,
                                        fontFamily = anton
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Slider(
                                        value = selectedMinMagnitude.toFloat(),
                                        onValueChange = { selectedMinMagnitude = it.toDouble() },
                                        valueRange = 1f..8f,
                                        steps = 13,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = primary,
                                            activeTrackColor = primary,
                                            inactiveTrackColor = Color.LightGray
                                        )

                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "1.0",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontFamily = anton
                                        )

                                        Text(
                                            text = "8.0",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontFamily = anton
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showStartDatePicker = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Start Date:",
                                        fontSize = 16.sp,
                                        fontFamily = anton
                                    )

                                    Text(
                                        text = selectedStartDate.format(
                                            DateTimeFormatter.ofPattern("MMM d, yyyy")
                                        ),
                                        color = primary
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showEndDatePicker = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "End Date:",
                                        fontSize = 16.sp,
                                        fontFamily = anton
                                    )

                                    Text(
                                        text = selectedEndDate.format(
                                            DateTimeFormatter.ofPattern("MMM d, yyyy")
                                        ),
                                        color = primary
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {

                                        val startTime = dateToMillis(selectedStartDate)
                                        val endTime = dateToMillis(selectedEndDate.plusDays(1))

                                        viewModel.loadFilteredEarthquakes(
                                            minMagnitude = selectedMinMagnitude,
                                            startTime = startTime,
                                            endTime = endTime
                                        )

                                        confirmedMinMagnitude = selectedMinMagnitude
                                        confirmedStartDate = selectedStartDate
                                        confirmedEndDate = selectedEndDate

                                        showFilterView = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primary
                                    )
                                ) {
                                    Text(
                                        text = "CONFIRM",
                                        fontFamily = anton,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                                Button(
                                    onClick = {
                                        showFilterView = false
                                        cancelFilterChanges()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Gray
                                    )
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontFamily = anton,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                        }

                    }


                }
            }
            Spacer(
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth()
            )
        }
        // START DATE PICKER
        if (showStartDatePicker) {

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedStartDate
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli(),

                selectableDates = object : SelectableDates {

                    override fun isSelectableDate(
                        utcTimeMillis: Long
                    ): Boolean {

                        val selectedDate = Instant
                            .ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        // Cannot select a future date
                        return !selectedDate.isAfter(LocalDate.now())
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = {
                    showStartDatePicker = false
                },

                confirmButton = {
                    TextButton(
                        onClick = {

                            datePickerState.selectedDateMillis?.let { millis ->

                                selectedStartDate = Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()

                                // If the new start date is after
                                // the current end date, move the end date
                                // to the new start date.
                                if (selectedEndDate.isBefore(selectedStartDate)) {
                                    selectedEndDate = selectedStartDate
                                }
                            }

                            showStartDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            showStartDatePicker = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }


// END DATE PICKER
        if (showEndDatePicker) {

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedEndDate
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli(),

                selectableDates = object : SelectableDates {

                    override fun isSelectableDate(
                        utcTimeMillis: Long
                    ): Boolean {

                        val selectedDate = Instant
                            .ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        // Cannot select before the start date
                        // or after today.
                        return !selectedDate.isBefore(selectedStartDate) &&
                                !selectedDate.isAfter(LocalDate.now())
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = {
                    showEndDatePicker = false
                },

                confirmButton = {
                    TextButton(
                        onClick = {

                            datePickerState.selectedDateMillis?.let { millis ->

                                selectedEndDate = Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                            }

                            showEndDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            showEndDatePicker = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen()
}