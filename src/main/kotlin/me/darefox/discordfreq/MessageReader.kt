package me.darefox.discordfreq

import java.io.File

data class Message(
    val header: String,
    val lines: List<String>
)

class MessageReader(file: File) {
    private val headerRegex = "^\\[\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d \\d\\d:\\d\\d] .+".toRegex()
    private val lineReader = file.bufferedReader()

    private var currentHeader: String? = null

    /**
     * Read messages by chunks, returns empty list if no messages are left to read
     */
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
                        val trimmed = textBuffer.trimEmptyEndLines()
                        if (trimmed.isNotEmpty()) {
                            chunks += Message(currentHeader!!, trimmed)
                        }

                        currentHeader = line
                        textBuffer.clear()
                    }
                    else -> currentHeader = line
                }
            } else if (currentHeader != null) {
                textBuffer += line.trim()
            }
        }

        if (currentHeader != null && textBuffer.isNotEmpty()) {
            val trimmed = textBuffer.trimEmptyEndLines()
            if (trimmed.isNotEmpty()) {
                chunks += Message(currentHeader!!, trimmed)
            }
        }

        return chunks
    }
}