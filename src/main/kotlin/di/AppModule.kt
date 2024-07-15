package di

import data.api.WeatherService
import data.api.WeatherServiceImpl
import data.repository.WeatherRepository
import org.koin.dsl.module

val appModule = module {
    single<WeatherService> { WeatherServiceImpl() }
    single<WeatherRepository> { WeatherRepository(get()) }
}
