package me.darefox.discordfreq

/**
 * Splits the elements of the array into N equal parts as evenly as possible.
 *
 * If the number of elements (N) is less than the specified number of parts (X),
 * it returns a nested array of N-sized lists, each containing one element.
 *
 * @param parts The number of parts to split the array into.
 * @return A list of lists containing the split elements.
 * @throws IllegalArgumentException if `parts` is less than or equal to zero.
 */
fun <T> List<T>.splitIntoParts(parts: Int): List<List<T>> {
    require(parts > 0) {
        "Parts should be greater than zero (was $parts)"
    }

    val partsList = buildList<MutableList<T>> {
        repeat(parts) {
            add(mutableListOf())
        }
    }

    for ((index, value) in this.withIndex()) {
        val partIndex = index % parts
        partsList[partIndex].add(value)
    }

    return partsList.filter { it.isNotEmpty() }
}

/**
 * Remove empty lines at the end of list
 */
fun List<String>.trimEmptyEndLines(): List<String> {
    var emptyLinesCount = 0
    for (line in reversed()) {
        if (line.isEmpty()) {
            emptyLinesCount++
        } else {
            break

        }
    }

    return slice(0 until (size - emptyLinesCount))
}