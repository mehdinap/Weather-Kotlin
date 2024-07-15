import di.appModule
import data.repository.WeatherRepository
import models.UiState
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.java.KoinJavaComponent.inject
import kotlinx.coroutines.*

fun main() {
    startKoin {
        modules(appModule)
    }

    val weatherRepository: WeatherRepository by inject(WeatherRepository::class.java)

    while (true) {
        println("Enter city name or latitude,longitude (or type 'exit' to quit):")
        val input = readlnOrNull()?.trim()

        if (input.equals("exit", ignoreCase = true)) {
            break
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
                        if (lat != null && long != null) {
                            weatherRepository.getWeatherByLatLong(lat, long)
                        } else {
                            UiState.Error("Invalid latitude or longitude.")
                        }
                    } else {
                        UiState.Error("Invalid input format. Use latitude,longitude format.")
                    }
                } else {
                    weatherRepository.getWeatherByCity(input)
                }
            }

            loadingJob.cancelAndJoin()

            when (result) {
                is UiState.Success -> println("\nWeather: ${result.data}")
                is UiState.Error -> println("\nError: ${result.message}")
            }
        }

        println("==================================\n")
    }

    stopKoin()
}
