package com.gharieb.cityfinder.data.repo

import android.content.Context
import com.gharieb.cityfinder.data.mapers.toDomain
import com.gharieb.cityfinder.data.models.CityDTO
import com.gharieb.cityfinder.domain.model.City
import com.gharieb.cityfinder.domain.repo.CityFinderIRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class CityFinderRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
): CityFinderIRepo {

    /**
     * reads the cities data from the JSON asset and returns them as domain entities.
     *
     * Flow:
     * 1. Opens the asset named by [DATA_SOURCE_FILE_NAME].
     * 2. Decodes the stream into `List<CityDTO>` via `kotlinx.serialization`.
     * 3. Maps the DTOs to domain `City` objects using `toDomain()`.
     *
     * Performance:
     * - Uses withContext(Dispatchers.IO) to avoid blocking the main thread.
     * - decodeFromStream parses directly from the input stream.
     */
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getAllCities(): List<City> = withContext(Dispatchers.IO) {
        context.assets.open(DATA_SOURCE_FILE_NAME).use { input ->
            val citiesDTO: List<CityDTO> = json.decodeFromStream(input)
            citiesDTO.toDomain()
        }
    }

}

/**
 * File name of the cities JSON inside the `assets/` directory.
 */
const val DATA_SOURCE_FILE_NAME = "cities.json"