package data.api

import models.WeatherInfo

interface WeatherService {
    suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo>
    suspend fun getWeatherByLatLong(Lat: Float, Long: Float): NetworkResult<WeatherInfo>

}