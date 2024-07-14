package data.models

data class WeatherInfo(
    val location: String,
    val condition: WeatherCondition,
    val requestTime: String
)
