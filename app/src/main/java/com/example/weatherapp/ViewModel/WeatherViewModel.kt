package com.example.weatherapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    fun updateCity(newCity: String) {
        _city.value = newCity
    }

    fun getWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState(isLoading = true)
            try {
                val response = RetrofitInstance.api.getWeather(
                    city = _city.value,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                )
                _uiState.value = WeatherUiState(weather = response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}