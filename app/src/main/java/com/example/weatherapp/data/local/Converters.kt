package com.example.weatherapp.data.local

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.WeatherResponse
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromWeatherResponse(value: WeatherResponse): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toWeatherResponse(value: String): WeatherResponse {
        return Gson().fromJson(value, WeatherResponse::class.java)
    }
}