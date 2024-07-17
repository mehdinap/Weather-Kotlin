package data.repository

import data.api.NetworkResult
import data.api.WeatherService
import models.UiState
import models.WeatherInfo

class WeatherRepository(private val weatherService: WeatherService) {

    suspend fun getWeatherByCity(city: String): UiState<WeatherInfo> {
        return when (val result = weatherService.getWeatherByCity(city)) {
            is NetworkResult.Success -> UiState.Success(result.data)
            is NetworkResult.Error -> UiState.Error(result.exception.message ?: "Unknown Error")
        }
    }
        // for mapping to data source not ui
    suspend fun getWeatherByLatLong(lat: Float, long: Float): UiState<WeatherInfo> {
        return when (val result = weatherService.getWeatherByLatLong(lat, long)) {
            is NetworkResult.Success -> UiState.Success(result.data)
            is NetworkResult.Error -> UiState.Error(result.exception.message ?: "Unknown Error")
        }
    }
}
