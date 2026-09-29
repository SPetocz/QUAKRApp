package com.petocz.quakrapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petocz.quakrapp.ui.theme.*

@Composable
fun MapScreen() {
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
                    .height(60.dp)
            )

            Text(
                text = "QUAKR",
                fontSize = 60.sp,
                fontFamily = anton,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
            )

            Box(
                modifier = Modifier
                    .size(width = 425.dp, height = 800.dp)
                    .clip(
                        RoundedCornerShape(30.dp))
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                    Text(
                        text = "Map Goes Here",
                        fontSize = 32.sp,
                        fontFamily = anton,
                        color = Color.White
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