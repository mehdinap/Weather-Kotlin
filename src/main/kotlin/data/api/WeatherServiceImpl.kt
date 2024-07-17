package data.api

import models.WeatherInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WeatherServiceImpl : WeatherService {
        // DI for HttpClient or constructor
        // default url CONTENT
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo> {
        return try {
            val response: HttpResponse = client.get(Api.BASE_URL) {
                parameter(Api.DEFAULT_FIELDS, cityName)
                parameter("key", Api.API_KEY)
            }       // status range 200 - ...
            if (response.status.value == 200) {
                val weatherInfo = response.body<WeatherInfo>()
                NetworkResult.Success(weatherInfo)
            } else {
                NetworkResult.Error(Exception("Failed to fetch weather data"))
            }   // dispacture ktor main-safe 4 form.
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun getWeatherByLatLong(Lat: Float, Long: Float): NetworkResult<WeatherInfo> {
        return try {
            val response: HttpResponse = client.get(Api.BASE_URL) {
                parameter(Api.DEFAULT_FIELDS, "$Lat,$Long")
                parameter("key", Api.API_KEY)
            }
            if (response.status.value == 200) {
                val weatherInfo = response.body<WeatherInfo>()
                NetworkResult.Success(weatherInfo)
            } else {
                NetworkResult.Error(Exception("Failed to fetch weather data"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}
