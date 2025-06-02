package com.eltonkola.model

import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

data class PostMetadata(
    val title: String = "Untitled",
    val date: LocalDate = LocalDate.MIN,
    val slug: String = "",
    val summary: String = "",
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val language: String = "en"
)

data class BlogPost(
    val metadata: PostMetadata,
    val content: String,
    val fileName: String
){
    val snippet get() = extractPreview(content)
}

fun extractPreview(body: String, maxLength: Int = 300): String {
    val firstParagraph = body.trim().split(Regex("\n\\s*\n")).firstOrNull()?.trim() ?: ""
    return if (firstParagraph.length <= maxLength) firstParagraph
    else firstParagraph.take(maxLength).substringBeforeLast(" ") + "..."
}

fun parseBlogPost(filePath: String): BlogPost {
    val thisFile = File(filePath)
    val lines = thisFile.readLines()
    var metadataLines = mutableListOf<String>()
    var contentLines = mutableListOf<String>()

    var index = 0

    // Detect frontmatter block at the top
    if (lines.isNotEmpty() && lines[0].trim() == "+++") {
        index = 1
        while (index < lines.size) {
            val line = lines[index].trim()
            if (line == "+++") {
                index++
                break
            }
            metadataLines.add(lines[index])
            index++
        }
    }

    // Everything after frontmatter (or from start if no frontmatter)
    while (index < lines.size) {
        contentLines.add(lines[index])
        index++
    }

    // Parse TOML-like frontmatter (simple key=value pairs)
    val metadataMap = mutableMapOf<String, String>()

    for (line in metadataLines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue
        val parts = trimmed.split("=", limit = 2)
        if (parts.size != 2) continue
        val key = parts[0].trim()
        var value = parts[1].trim()

        // Remove quotes if any
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.removeSurrounding("\"")
        } else if (value.startsWith("[") && value.endsWith("]")) {
            // For lists, keep as is (e.g. ["tag1", "tag2"])
            // We'll parse this later as comma separated without quotes
            value = value.removeSurrounding("[", "]")
        }

        metadataMap[key] = value
    }

    fun parseDateOrNull(value: String?): LocalDate ?  =
        try {
            if (value == null) null else LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            null
        }

    // Parse tags as list from CSV-like string
    val tags = metadataMap["tags"]?.split(",")
        ?.map { it.trim().removeSurrounding("\"") }
        ?: emptyList()

    val content = contentLines.joinToString("\n")
    val metadata = PostMetadata(
        title = metadataMap["title"] ?: thisFile.nameWithoutExtension,
        date = parseDateOrNull(metadataMap["date"]) ?: thisFile.lastModifiedDate(),
        slug = metadataMap["slug"] ?: "",
        summary = metadataMap["summary"] ?: extractPreview(content),
        tags = tags,
        draft = metadataMap["draft"]?.toBoolean() ?: false,
        language = metadataMap["language"] ?: "en"
    )

    return BlogPost(
        metadata = metadata,
        content = content,
        fileName = File(filePath).name
    )
}

fun File.lastModifiedDate(): LocalDate =
    Instant.ofEpochMilli(this.lastModified())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
