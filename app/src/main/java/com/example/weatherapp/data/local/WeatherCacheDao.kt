package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.WeatherCacheEntity

@Dao
interface WeatherCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherCache: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE cityName = :cityName")
    suspend fun getWeatherByCity(cityName: String): WeatherCacheEntity?
}