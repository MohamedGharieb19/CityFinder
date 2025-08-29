package com.gharieb.cityfinder.data.mapers

import com.gharieb.cityfinder.data.models.CityDTO
import com.gharieb.cityfinder.data.models.CoordinatesDTO
import com.gharieb.cityfinder.domain.model.City
import com.gharieb.cityfinder.domain.model.Coordinates
import java.util.Locale

fun CityDTO.toDomain() = City(
    id = id,
    name = name.plus(", ").plus(country),
    flagEmoji = countryCodeToFlagEmoji(country),
    coordinates = coordinates.toDomain()
)

fun CoordinatesDTO.toDomain() = Coordinates(
    longitude = longitude,
    latitude = latitude
)

fun List<CityDTO>.toDomain(): List<City> = map { it.toDomain() }

/** @return flag emoji for the given country code. */
private fun countryCodeToFlagEmoji(code: String): String {
    val cc = code.trim().uppercase(Locale.ROOT)
    if (cc.length != 2) return "🏳️"
    val base = 0x1F1E6 - 'A'.code
    val a = Character.toChars(base + cc[0].code)
    val b = Character.toChars(base + cc[1].code)
    return String(a) + String(b)
}

