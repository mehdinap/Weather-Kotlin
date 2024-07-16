import di.appModule
import data.repository.WeatherRepository
import storegs.HistoryStorage
import models.UiState
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject
import kotlinx.coroutines.*
import models.WeatherInfo

fun isValidLatitude(lat: Float): Boolean = lat in -90.0..90.0
fun isValidLongitude(long: Float): Boolean = long in -180.0..180.0


fun main() {
    startKoin {
        modules(appModule)
    }

    val weatherRepository: WeatherRepository by inject(WeatherRepository::class.java)
    val historyStorage: HistoryStorage by inject(HistoryStorage::class.java)

    while (true) {
        println("Enter city name or latitude,longitude (or type 'exit' to quit, 'history' to view search history):")
        print("\t")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            break
        }

        if (input.equals("history", ignoreCase = true)) {
            runBlocking {
                val history = historyStorage.getSearchHistory()
                history.forEach { (query, result) ->
                    println("\t$query, Result: ${result.location.request_time} , ${result.current.condition.text} - code: ${result.current.condition.code}")
                }
            }
            println("==================================\n")
            continue
        }

        if (input.isNullOrEmpty()) {
            println("Invalid input. Please enter a valid city name or latitude,longitude.")
            println("==================================\n")
            continue
        }

        runBlocking {
            val loadingJob = launch(Dispatchers.Default) {
                print("loading ")
                while (isActive) {
                    print(".")
                    delay(200)
                }
            }

            val result = withContext(Dispatchers.Default) {
                if (input.contains(",")) {
                    val coordinates = input.split(",")
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
                    weatherRepository.getWeatherByCity(input)
                }
            }

            loadingJob.cancelAndJoin()

            when (result) {
                is UiState.Success -> {
                    val weather = result.data to WeatherInfo
                    println("\n${weather.first.location.request_time} : ${weather.first.location.name} current weather is ${weather.first.current.condition.text}\n")
                    historyStorage.saveSearch(input, result.data)
                }

                is UiState.Error -> println("\nError: ${result.message}\n")
            }
        }

        println("==================================\n")
    }

    stopKoin()
}
