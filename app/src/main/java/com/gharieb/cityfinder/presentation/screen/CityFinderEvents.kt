package com.gharieb.cityfinder.presentation.screen

sealed class CityFinderEvents {
    data class OnQuerySearchChanged(val querySearch: String) : CityFinderEvents()
}