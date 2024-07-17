package storegs

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.WeatherInfo
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FileHistoryStorage(private val filePath: String) : HistoryStorage {
    // can json be DI
    // dispacture also can be DI
    private val json = Json { ignoreUnknownKeys = true }
    private val lock = ReentrantLock()
    private val file = File(filePath)
    private var cache: MutableList<Pair<String, WeatherInfo>> = mutableListOf()

    // for pre-start init is not recommended do in like start method
    init {
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("[]")
        }
        loadCacheFromFile()
    }

    override suspend fun saveSearch(query: String, result: WeatherInfo) {
        lock.withLock {
            cache.add(query to result)
            // Async si nor good place.
//            saveToFileAsync()
            writeHistoryToFile()

        }
    }

    override suspend fun getSearchHistory(): List<Pair<String, WeatherInfo>> {
        return lock.withLock {
            cache.toList()
        }
    }

    private fun loadCacheFromFile() {
        lock.withLock {
            val fileContent = file.readText()
            if (fileContent.isNotBlank()) {
                try {
                    cache = json.decodeFromString<List<Pair<String, WeatherInfo>>>(fileContent).toMutableList()
                } catch (e: Exception) {
                    cache = mutableListOf()
                }
            } else {
                cache = mutableListOf()
            }
        }
    }

    private fun saveToFileAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            writeHistoryToFile()
        }
    }

    private fun writeHistoryToFile() {
        // nested lock is not good   -->  deadLock
        //        lock.withLock {
            file.writeText(json.encodeToString(cache))
//        }
    }
}
