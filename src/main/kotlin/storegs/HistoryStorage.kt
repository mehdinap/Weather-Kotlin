package storegs

import models.WeatherInfo

interface HistoryStorage {
    suspend fun saveSearch(query: String, result: WeatherInfo)
    suspend fun getSearchHistory(): List<Pair<String, WeatherInfo>>
}
