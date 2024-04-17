package me.darefox

import kotlinx.coroutines.*
import java.io.File

object FrequencyFileGenerator {
    private val calculationScope = CoroutineScope(Dispatchers.Default + CoroutineName("FrequencyCalculationScope"))

    suspend fun generate(file: File, saveTo: File) {
        require(file.extension == "txt") {
            "Program supports only .txt format of discord chat export"
        }

        val frequencySum = mutableMapOf<String, Long>()
        val reader = MessageReader(file)

        while (true) {
            val amountMessages = 1000
            println("\nReading $amountMessages messages")
            val messages = reader.readMessages(amountMessages)
            if (messages.isEmpty()) break

            println("Cleaning messages")
            val splitCleanMessages = MessageCleaner.clean(messages).splitIntoParts(RuntimeInfo.cpuThreads)
            println("Calculating frequencies")
            val frequencies = splitCleanMessages.map { list ->
                calculationScope.async {
                    analyzeFrequency(list)
                }
            }.awaitAll()

            println("Combining frequencies")
            for (frequency in frequencies) {
                frequency.entries.forEach { current ->
                    val sumValue = frequencySum[current.key] ?: 0
                    frequencySum[current.key] = sumValue + current.value
                }
            }
        }

        println("Converting frequencies to sorted list")
        val list = frequencyToSortedList(frequencySum)
        withContext(Dispatchers.IO) {
            println("Creating new file")
            saveTo.createNewFile()
            println("Writing to new file")
            val listAsText = list.joinToString(
                separator = "\n",
                prefix = "",
                postfix = ""
            ) {
                "${it.first} ${it.second}"
            }
            saveTo.writeText(listAsText)
        }
    }

    private fun frequencyToSortedList(freq: Map<String, Long>): List<Pair<String, Long>> {
        return freq.entries.map {
            it.key to it.value
        }.sortedByDescending { it.second }
    }

    private fun convertToCleanWords(lines: List<String>): List<String> {
        val splitRegex = "[-.?!=;:(),/#%^&*'\"@\\s]".toRegex()
        return lines
            .map {
                it.lowercase().split(splitRegex)
            }.flatten()
            .filter { it.isNotEmpty() }
    }

    private fun analyzeFrequency(messages: List<CleanMessage>): MutableMap<String, Long> {
        val frequency = mutableMapOf<String, Long>()
        for (message in messages) {
            val words = convertToCleanWords(message.cleanLines)
            for (word in words) {
                val count = frequency[word] ?: 0
                frequency[word] = count + 1
            }
        }
        return frequency
    }
}