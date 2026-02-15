package com.example.weatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.WeatherResponse
import kotlin.math.roundToInt

@Composable
fun WeatherResultSection(weather: WeatherResponse) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = weather.name, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${weather.main.temp.roundToInt()}°C", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = weather.weather.first().description, fontSize = 20.sp)
    }
}