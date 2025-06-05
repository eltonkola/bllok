package com.eltonkola.model

import Nav
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.space.SFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
    val fileName: String,
    val link: String
){

    val snippet get() = extractPreview(content)
}

fun String.toBreadcrumbNavs(): List<Nav> {
    val parts = this.trim('/').split('/')
    val navs = mutableListOf<Nav>()
    var currentPath = ""

    for ((index, part) in parts.withIndex()) {
        val cleanPart = part.removeSuffix(".html")
        val name = cleanPart.replace("-", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

        currentPath += "/$part"
        navs.add(Nav(name = name, link = currentPath))
    }

    if(navs.isNotEmpty()) navs.removeLast()

    return navs
}

fun extractPreview(body: String, maxLength: Int = 300): String {
    val firstParagraph = body.trim().split(Regex("\n\\s*\n")).firstOrNull()?.trim() ?: ""
    return if (firstParagraph.length <= maxLength) firstParagraph
    else firstParagraph.take(maxLength).substringBeforeLast(" ") + "..."
}

fun BlogFile.parseBlogPost(): BlogPost {
    val thisFile = File(this.file.path)
    val lines = thisFile.readLines()

    val metadataLines = mutableListOf<String>()
    val contentLines = mutableListOf<String>()

    var index = 0

    // Detect YAML front matter with '---'
    if (lines.isNotEmpty() && lines[0].trim() == "---") {
        index = 1
        while (index < lines.size) {
            val line = lines[index].trimEnd()
            if (line.trim() == "---") {
                index++
                break
            }
            metadataLines.add(line)
            index++
        }
    }

    // Remainder is markdown content
    while (index < lines.size) {
        contentLines.add(lines[index])
        index++
    }

    val metadataMap = parseYamlFrontMatter(metadataLines)

    fun parseDateOrNull(value: String?): LocalDate? =
        try {
            value?.let { LocalDate.parse(it) }
        } catch (e: Exception) {
            null
        }

    val tags = metadataMap["tags"]?.let {
        if (it.startsWith("[") && it.endsWith("]")) {
            it.removeSurrounding("[", "]").split(",").map { tag -> tag.trim().removeSurrounding("\"") }
        } else {
            emptyList()
        }
    } ?: emptyList()

    val content = contentLines.joinToString("\n")

    val metadata = PostMetadata(
        title = metadataMap["title"] ?: thisFile.nameWithoutExtension,
        date = parseDateOrNull(metadataMap["date"]) ?: thisFile.lastModifiedDate(),
        slug = metadataMap["slug"] ?: "",
        summary = metadataMap["summary"] ?: extractPreview(content),
        tags = tags,
        draft = metadataMap["draft"]?.toBooleanStrictOrNull() ?: false,
        language = metadataMap["language"] ?: "en"
    )

    val flavour = GFMFlavourDescriptor()
//    val flavour = SFMFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(content)
    val html = HtmlGenerator(content, parsedTree, flavour).generateHtml()

    val updatedHtml = html.replace(
        Regex("""<a\s+href="((?!https?:|mailto:|#)[^"]+?)(?<!\.html)">""")
    ) { matchResult ->
        val href = matchResult.groupValues[1]
        """<a href="${href}.html">"""
    }
        .replace("<body>", "")
        .replace("</body>", "")

    return BlogPost(
        metadata = metadata,
        content = updatedHtml,
        fileName = thisFile.nameWithoutExtension,
        link = createPostLink(thisFile)
    )
}

fun BlogFile.createPostLink(thisFile: File) : String {
    return if(this.parent != null){
        "${this.parent.path + "/"}${thisFile.nameWithoutExtension}.html"
    }else{
        "${thisFile.nameWithoutExtension}.html"
    }
}


fun File.lastModifiedDate(): LocalDate =
    Instant.ofEpochMilli(this.lastModified())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()


fun parseYamlFrontMatter(lines: List<String>): Map<String, String> {
    val map = mutableMapOf<String, String>()
    var currentKey: String? = null
    var listBuffer: MutableList<String>? = null

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue

        when {
            trimmed.startsWith("- ") && currentKey != null -> {
                listBuffer?.add(trimmed.removePrefix("- ").trim())
            }

            ":" in trimmed -> {
                val (key, rawValue) = trimmed.split(":", limit = 2).map { it.trim() }
                currentKey = key

                if (rawValue.isEmpty()) {
                    // Start of list
                    listBuffer = mutableListOf()
                } else {
                    map[key] = rawValue.removeSurrounding("\"")
                    listBuffer = null
                }
            }

            else -> {
                // Skip unknown line
            }
        }

        if (currentKey != null && listBuffer != null) {
            map[currentKey!!] = listBuffer!!.joinToString(",", prefix = "[", postfix = "]")
        }
    }

    return map
}

