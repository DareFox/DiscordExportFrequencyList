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