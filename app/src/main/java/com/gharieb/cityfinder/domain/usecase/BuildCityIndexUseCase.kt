package com.gharieb.cityfinder.domain.usecase

import com.gharieb.cityfinder.domain.model.CityIndex
import com.gharieb.cityfinder.domain.model.CityIndexBuilder
import com.gharieb.cityfinder.domain.repo.CityFinderIRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds and caches the [CityIndex] for fast prefix search.
 *
 * - Loads raw cities from [CityFinderIRepo].
 * - Delegates sorting + key creation to [CityIndexBuilder].
 * - Caches the built index in-memory to avoid rebuilding on every search.
 *
 * This use case is a `@Singleton`, so the cache is shared across the app.
 */
@Singleton
class BuildCityIndexUseCase @Inject constructor(
    private val repo: CityFinderIRepo
) {
    @Volatile
    private var cachedCityIndex: CityIndex? = null

    /**
     * Build (or return) the [CityIndex].
     * @return The current, fully built [CityIndex].
     */
    suspend operator fun invoke(): CityIndex = withContext(Dispatchers.Default) {
        cachedCityIndex?.let {
            return@withContext it
        } ?: run {
            val cities = repo.getAllCities()
            val builtCityIndex = CityIndexBuilder.build(cities)
            cachedCityIndex = builtCityIndex
            builtCityIndex
        }
    }

    /**
     * Returns the cached [CityIndex] if available, or `null` if it hasn't been built yet.
     */
    fun cachedCityIndex(): CityIndex? = cachedCityIndex
}
