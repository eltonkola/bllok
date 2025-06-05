package com.eltonkola.model

import java.io.File

data class BlogConfig(
    val postsPerPage: Int,
    val baseUrl: String,
    val websiteName: String,
    val websiteDescription: String,
    val feedEmail: String,
    val feedEmailRealName: String,
    val socials: List<String>,
    val websiteCopyright: String,
    val language: String
)
fun parseYamlConfig(filePath: String): BlogConfig {
    val map = mutableMapOf<String, Any>()
    val lines = File(filePath).readLines()

    var currentKey: String? = null
    var currentList: MutableList<String>? = null

    for ((index, lineRaw) in lines.withIndex()) {
        val line = lineRaw.trimEnd()
        if (line.isBlank() || line.trimStart().startsWith("#")) continue

        val isLastLine = index == lines.lastIndex

        if (line.trimStart().startsWith("- ") && currentKey != null) {
            // Continue a list
            val value = line.trimStart().removePrefix("- ").trim().removeSurrounding("\"")
            currentList?.add(value)

            // If it's the last line, we need to finalize the list
            if (isLastLine && currentList != null) {
                map[currentKey] = currentList.toList()
                currentKey = null
                currentList = null
            }

            continue
        }

        // New key detected: finalize any previous list
        if (currentKey != null && currentList != null) {
            map[currentKey] = currentList.toList()
            currentKey = null
            currentList = null
        }

        val parts = line.split(":", limit = 2)
        if (parts.size == 2) {
            val key = parts[0].trim()
            val rawValue = parts[1].trim()

            if (rawValue.isEmpty()) {
                // Start a new list
                currentKey = key
                currentList = mutableListOf()
            } else {
                val value = rawValue.removeSurrounding("\"")
                map[key] = value
            }
        }
    }

    // Final catch for dangling list at EOF
    if (currentKey != null && currentList != null) {
        map[currentKey] = currentList.toList()
    }

    return BlogConfig(
        postsPerPage = (map["postsPerPage"] as? String)?.toIntOrNull() ?: 10,
        baseUrl = map["baseUrl"] as? String ?: "",
        websiteName = map["websiteName"] as? String ?: "",
        websiteDescription = map["websiteDescription"] as? String ?: "",
        feedEmail = map["feedEmail"] as? String ?: "",
        feedEmailRealName = map["feedEmailRealName"] as? String ?: "",
        socials = (map["socials"] as? List<*>)?.map { it.toString() } ?: emptyList(),
        websiteCopyright = map["websiteCopyright"] as? String ?: "",
        language = map["language"] as? String ?: "en"
    )
}
