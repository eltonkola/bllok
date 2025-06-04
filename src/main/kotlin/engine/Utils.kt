package com.eltonkola.engine

import com.eltonkola.model.BlogFile
import com.eltonkola.model.lastModifiedDate
import com.eltonkola.model.parseBlogPost
import kotlinx.coroutines.*
import java.time.LocalDate

suspend fun getCreationDatesParallel(files: List<BlogFile>): Map<BlogFile, LocalDate> = coroutineScope {
    val jobs = files.map { file ->
        async(Dispatchers.IO) {
            val post = file.parseBlogPost()
            file to post.metadata.date
        }
    }
    jobs.awaitAll().toMap()
}

fun  List<BlogFile>.sort() :  List<BlogFile> {

    val creationMap = runBlocking {
        getCreationDatesParallel(this@sort)
    }

    return this.sortedByDescending { creationMap[it] ?: it.file.lastModifiedDate() }
}