package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.local.WeatherCacheDao
import com.example.weatherapp.data.model.WeatherCacheEntity
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitInstance

class WeatherRepository(private val weatherCacheDao: WeatherCacheDao) {

    private val CACHE_DURATION_MS = 30 * 60 * 1000 // 30 minuuttia

    suspend fun getWeather(city: String): Result<WeatherResponse> {
        // 1. Yritä hakea dataa välimuistista
        val cachedData = weatherCacheDao.getWeatherByCity(city)
        val now = System.currentTimeMillis()

        if (cachedData != null && (now - cachedData.timestamp) < CACHE_DURATION_MS) {
            return Result.success(cachedData.weatherData)
        }

        // 2. Jos välimuistidataa ei ole tai se on vanhentunutta, hae API:sta
        return try {
            val apiResponse = RetrofitInstance.api.getWeather(
                city = city,
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            )
            // 3. Tallenna onnistunut haku välimuistiin
            val newCache = WeatherCacheEntity(cityName = city, weatherData = apiResponse)
            weatherCacheDao.insert(newCache)
            Result.success(apiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}