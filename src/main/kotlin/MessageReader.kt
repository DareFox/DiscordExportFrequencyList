package me.darefox

import java.io.File

data class Message(
    val header: String,
    val lines: List<String>
)

class MessageReader(file: File) {
    private val headerRegex = "^\\[\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d \\d\\d:\\d\\d] .+".toRegex()
    private val lineReader = file.bufferedReader()

    private var currentHeader: String? = null

    fun readMessages(amount: Int): List<Message> {
        val chunks = mutableListOf<Message>()

        var readLine: String? = null
        val textBuffer = mutableListOf<String>()

        while (chunks.size < amount && lineReader.readLine().also { readLine = it } != null) {
            val line = readLine!!
            val isHeader = headerRegex.matches(line)
            if (isHeader) {
                when {
                    currentHeader != null -> {
                        val trimmed = trimEmptyEndLines(textBuffer)
                        require(trimmed.isNotEmpty()) { "Text is empty" }

                        chunks += Message(currentHeader!!, trimmed)

                        currentHeader = line
                        textBuffer.clear()
                    }
                    else -> currentHeader = line
                }
            } else if (currentHeader != null) {
                textBuffer += line.trim()
            }
        }

        if (currentHeader != null) {
            val trimmed = trimEmptyEndLines(textBuffer)
            require(trimmed.isNotEmpty()) { "Text is empty" }
            chunks += Message(currentHeader!!, trimmed)
        }

        return chunks
    }

    private fun trimEmptyEndLines(lines: List<String>): List<String> {
        var emptyLinesCount = 0
        for (line in lines.asReversed()) {
            if (line.isEmpty()) {
                emptyLinesCount++
            } else {
                break

            }
        }

        return lines.slice(0 until (lines.size - emptyLinesCount))
    }
}