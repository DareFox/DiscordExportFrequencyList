package me.darefox.discordfreq

import kotlinx.coroutines.*

data class CleanMessage(
    val header: String,
    val cleanLines: List<String>,
)

object MessageCleaner {
    val scope = CoroutineScope(Dispatchers.Default + CoroutineName("MessageCleanerScope"))
    /**
     * Remove embeds, attachments, urls in messages leaving only text words
     */
    suspend fun clean(messages: List<Message>): List<CleanMessage> {
        val split = messages.splitIntoParts(RuntimeInfo.cpuThreads)

        val asyncClean = split.map { list ->
            scope.async {
                val cleanList = mutableListOf<CleanMessage>()
                for (message in list) {
                    val cleanedLines =
                        message.removeURLs().removeAttachmentAndEmbed()
                            .lines.trimEmptyEndLines()

                    if (cleanedLines.isNotEmpty()) {
                        cleanList += CleanMessage(message.header, cleanedLines)
                    }
                }
                cleanList
            }
        }

        return asyncClean.awaitAll().flatten()
    }

    private fun Message.removeURLs(): Message {
        val urlLikeRegex = "[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?".toRegex()
        return Message(header, lines.map {
            it.replace(urlLikeRegex, "")
        })
    }

    private fun Message.removeAttachmentAndEmbed(): Message {
        val newLines = mutableListOf<String>()
        val discordTags = listOf(
            "{Stickers}",
            "{Embed}",
            "{Attachments}"
        )
        for (line in lines) {
            if (line in discordTags) {
                break
            } else {
                newLines += line
            }
        }
        return Message(header, newLines)
    }
}