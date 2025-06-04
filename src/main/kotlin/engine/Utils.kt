package com.eltonkola.engine

import com.eltonkola.model.BlogFile
import jdk.internal.vm.ThreadContainers.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.format.DateTimeFormatter
import kotlin.collections.map
import java.time.Instant
val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

suspend fun getGitCreationDatesParallel(files: List<File>): Map<File, Instant> = coroutineScope {
    val jobs = files.map { file ->
        async(Dispatchers.IO) {
            val process = ProcessBuilder(
                "git", "log", "--diff-filter=A", "--follow", "--format=%aI", "--", file.path
            )
                .directory(file.parentFile)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readLines()
            val dateStr = output.firstOrNull()
            val date = try {
                dateStr?.let { Instant.from(isoFormatter.parse(it)) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            file to (date ?: Instant.EPOCH)
        }
    }
    jobs.awaitAll().toMap()
}

fun  List<BlogFile>.sort() :  List<BlogFile> {
    val fileList = this.map { it.file }
    val creationMap = runBlocking {
        getGitCreationDatesParallel(fileList)
    }

    return this.sortedByDescending { creationMap[it.file] ?:  Instant.ofEpochMilli(it.file.lastModified()) }
}