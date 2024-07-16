package di

import data.api.WeatherService
import data.api.WeatherServiceImpl
import data.repository.WeatherRepository
import org.koin.dsl.module
import storegs.FileHistoryStorage
import storegs.HistoryStorage

val appModule = module {
    single<WeatherService> { WeatherServiceImpl() }
    single<WeatherRepository> { WeatherRepository(get()) }
    single<HistoryStorage> { FileHistoryStorage("search_history.json") }
}
