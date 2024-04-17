package me.darefox

import java.io.File

suspend fun main(args: Array<String>) {
    val argsAsList = args.toList()
    if (argsAsList.isEmpty()) {
        println("Arguments: [inputfile.txt] [outputfile (optional)]")
        return
    }

    val inputFile = File(argsAsList[0])

    val outputArg = argsAsList.getOrNull(1)
    val outputFile = if (outputArg == null) {
        createNeighborFile(inputFile)
    } else {
        File(outputArg)
    }

    FrequencyFileGenerator.generate(inputFile, outputFile)
}

private fun createNeighborFile(file: File): File {
    val fileName = file.nameWithoutExtension + "-freq-list"
    val parentDir = file.parentFile
    val newFileName = "$fileName.txt"
    return File(parentDir, newFileName)
}