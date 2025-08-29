package com.gharieb.cityfinder.domain.repo

import com.gharieb.cityfinder.domain.model.City

interface CityFinderIRepo {
    /**
     * @return a list of [City] domain model.
     */
    suspend fun getAllCities(): List<City>
}