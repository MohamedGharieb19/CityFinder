package com.gharieb.cityfinder.domain.model

import java.text.Normalizer
import java.util.Locale

/**
 * Converts a human-readable string into a stable, searchable key.
 */
fun normalize(raw: String): String {
    val decomposed = Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
    val stripped = StringBuilder(decomposed.length)
    for (char in decomposed) if (Character.getType(char) != Character.NON_SPACING_MARK.toInt()) stripped.append(char)
    return stripped.toString().lowercase(Locale.ROOT)
}

data class CityIndex(
    val cities: List<City>,
    private val keys: Array<String>
) {
    private val size: Int get() = keys.size

    /**
     * Returns the first index in [0, size] whose key is >= [target].
     * lower-bound binary search.
     * Complexity: O(log n).
     */
    private fun lowerBound(target: String): Int {
        var lowIndex = 0
        var highIndex = size
        while (lowIndex < highIndex) {
            val midIndex = (lowIndex + highIndex) ushr 1
            if (keys[midIndex] < target) lowIndex = midIndex + 1 else highIndex = midIndex
        }
        return lowIndex
    }

    /**
     * Returns the first index in [0, size] that is greater than any string starting with [prefix].
     * Complexity: O(log n).
     */
    private fun upperBound(prefix: String): Int {
        val upperMarker = prefix + '\uFFFF'
        var lowIndex = 0
        var highIndex = size
        while (lowIndex < highIndex) {
            val midIndex = (lowIndex + highIndex) ushr 1
            if (keys[midIndex] <= upperMarker) lowIndex = midIndex + 1 else highIndex = midIndex
        }
        return lowIndex
    }

    /**
     * Computes the [start, end] slice of [cities] whose normalized names start with [query]
     * Complexity: O(log n)
     */
    fun rangeFor(query: String): IntRange {
        if (query.isBlank()) return keys.indices
        val normalizedQuery = normalize(query)
        val start = lowerBound(normalizedQuery)
        val end = upperBound(normalizedQuery)
        return if (start < end) start .. end else IntRange.EMPTY
    }
}

/**
 * Builds a sorted [CityIndex] once from raw cities.
 *
 * - Sorts by the normalized name.
 * - Builds a parallel array of normalized keys.
 * - Keeps both cities and keys aligned by index.
 *
 * Complexity: O(n log n) due to sorting.
 */
object CityIndexBuilder {
    fun build(raw: List<City>): CityIndex {
        val sorted = raw.sortedBy { normalize(it.name) }
        val keys = Array(sorted.size) { index -> normalize(sorted[index].name) }
        return CityIndex(sorted, keys)
    }
}
