package com.gharieb.cityfinder.domain.usecase

import com.gharieb.cityfinder.domain.model.City
import javax.inject.Inject

/**
 * Executes a fast **prefix search**
 *
 * Performance
 * - Query time: `O(log n) + O(k)` where `k` is the number of items you display.
 * - Memory: reuses the same backing `List<City>` from the index no result copying.
 */
class SearchCitiesUseCase @Inject constructor(
    private val buildIndex: BuildCityIndexUseCase
) {
    data class Result(val source: List<City>, val start: Int, val end: Int) {
        val size get() = end - start
        fun itemAt(i: Int): City = source[start + i]
        companion object { fun empty() = Result(emptyList(), 0, 0) }
    }

    /**
     * @param querySearch User input.
     * @return A [Result] describing the start, end & cities to render.
     */
    suspend operator fun invoke(querySearch: String): Result {
        val cityIndex = buildIndex.cachedCityIndex() ?: buildIndex()
        val range = cityIndex.rangeFor(querySearch)
        return if (range.isEmpty()) Result.empty() else Result(cityIndex.cities, range.first, range.last)
    }
}
