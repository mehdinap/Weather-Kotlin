package ui

import data.repository.WeatherRepository
import storegs.HistoryStorage
import models.UiState
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

const val lineBreaker = "==================================\n"

class WeatherApplicationImpl : WeatherApplication {
    private val weatherRepository: WeatherRepository by inject(WeatherRepository::class.java)
    private val historyStorage: HistoryStorage by inject(HistoryStorage::class.java)

    override fun executeCommand(command: String) {
        when {
            command.equals("exit", ignoreCase = true) -> {
                println("Exiting the application...")
                return
            }

            command.equals("history", ignoreCase = true) -> {
                runBlocking {
                    val history = historyStorage.getSearchHistory()
                    history.forEach { (query, result) ->
                        println("\t$query, Result: ${result.location.localtime} , ${result.current.condition.text} - code: ${result.current.condition.code}")
                    }
                }
                println(lineBreaker)
            }

            command.isNotBlank() -> {
                runBlocking {
                    val loadingJob = launch(Dispatchers.Default) {
                        print("loading ")
                        while (isActive) {
                            print(".")
                            delay(200)
                        }
                    }

                    val result = withContext(Dispatchers.Default) {
                        if (command.contains(",")) {
                            val coordinates = command.split(",")
                            if (coordinates.size == 2) {
                                val lat = coordinates[0].toFloatOrNull()
                                val long = coordinates[1].toFloatOrNull()
                                if (lat != null && long != null && isValidLatitude(lat) && isValidLongitude(long)) {
                                    weatherRepository.getWeatherByLatLong(lat, long)
                                } else {
                                    UiState.Error("Invalid latitude or longitude.\n")
                                }
                            } else {
                                UiState.Error("Invalid input format. Use latitude,longitude format.\n")
                            }
                        } else {
                            weatherRepository.getWeatherByCity(command)
                        }
                    }

                    loadingJob.cancelAndJoin()

                    when (result) {
                        is UiState.Success -> {
                            val weather = result.data
                            println("\n${weather.location.localtime} : ${weather.location.name} current weather is ${weather.current.condition.text}\n")
                            historyStorage.saveSearch(command, weather)
                        }

                        is UiState.Error -> println("\nError: ${result.message}\n")
                    }
                }
                println(lineBreaker)
            }

            else -> {
                println("Invalid input. Please enter a valid city name or latitude,longitude.")
                println(lineBreaker)
            }
        }
    }

    private fun isValidLatitude(lat: Float): Boolean = lat in -90.0..90.0
    private fun isValidLongitude(long: Float): Boolean = long in -180.0..180.0
}
