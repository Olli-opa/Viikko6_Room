package com.example.weatherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val cityName: String,
    val weatherData: WeatherResponse,
    val timestamp: Long = System.currentTimeMillis()
)