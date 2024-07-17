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

            command.contains("history") -> {
                runBlocking {
                    if (command.startsWith("history")) {
                        runBlocking {
                            val parts = command.split(" ")
                            val history = historyStorage.getSearchHistory()

                            if (parts.size == 1) {
                                // Show all queries
                                history.forEach { (query, result) ->
                                    println("\t$query, Result: ${result.location.name}, ${result.location.localtime}, ${result.current.condition.text} - code: ${result.current.condition.code}")
                                }
                            } else {
                                // Show specific query
                                val query = parts[1]
                                val filteredHistory = history.filter { (storedQuery, _) ->
                                    storedQuery == query
                                }

                                if (filteredHistory.isNotEmpty()) {
                                    filteredHistory.forEach { (storedQuery, result) ->
                                        println("\t$storedQuery, Result: ${result.location.name}, ${result.location.localtime}, ${result.current.condition.text} - code: ${result.current.condition.code}")
                                    }
                                } else {
                                    println("No history found for query: $query")
                                }
                            }
                        }
                    }

//                    val history = historyStorage.getSearchHistory()
//                    history.forEach { (query, result) ->
//                        println("\t$query, Result: ${result.location.name} ,${result.location.localtime} , ${result.current.condition.text} - code: ${result.current.condition.code}")
//                    }
                }
                println(lineBreaker)

            }

            // non relation models is defines in any pattern package.
            // storage -> data
            // models break.


            command.isNotBlank() -> {
                runBlocking {
                    // default is good for loop form by overhead and short time suspend
                    // i/o is good for
                    // Dispatchers for DI
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
