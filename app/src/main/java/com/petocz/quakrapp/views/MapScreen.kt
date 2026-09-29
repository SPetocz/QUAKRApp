package com.petocz.quakrapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petocz.quakrapp.R
import com.petocz.quakrapp.map.EarthquakeMap
import com.petocz.quakrapp.ui.theme.*
import com.petocz.quakrapp.map.*
import org.maplibre.android.maps.MapLibreMap


@Composable
fun MapScreen() {

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
                    .height(40.dp)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(
                        RoundedCornerShape(40.dp))
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(40.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                EarthquakeMap(
                    onMapReady = {
                        map -> mapLibreMap = map
                    }
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