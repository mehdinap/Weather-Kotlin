import di.appModule
import ui.WeatherApplication
import ui.WeatherApplicationImpl
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main() {
    startKoin {
        modules(appModule)
    }

    val weatherApp: WeatherApplication = WeatherApplicationImpl()
    while (true) {
        println("Enter city name or latitude,longitude (or type 'exit' to quit, 'history' to view search history):")
        print("\t")
        val input = readlnOrNull()?.trim()

        weatherApp.executeCommand(input ?: "")

        if (input.equals("exit", ignoreCase = true)) {
            break
        }
    }

    stopKoin()
}
