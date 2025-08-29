package com.gharieb.cityfinder.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDTO(
    @SerialName("_id")
    val id: Int,
    val name: String,
    val country: String,
    @SerialName("coord")
    val coordinates: CoordinatesDTO
)

@Serializable
data class CoordinatesDTO(
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double
)