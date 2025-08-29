package com.gharieb.cityfinder.domain.model

data class City(
    val id: Int,
    val name: String,
    val flagEmoji: String,
    val coordinates: Coordinates
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
