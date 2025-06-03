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
    val currentList = mutableListOf<String>()

    for (line in lines.map { it.trimEnd() }) {
        if (line.isBlank() || line.startsWith("#")) continue

        // List continuation
        if (line.startsWith("- ") && currentKey != null) {
            val value = line.removePrefix("- ").trim().removeSurrounding("\"")
            currentList.add(value)
            map[currentKey] = currentList.toList()
        }
        // New key-value or new list start
        else {
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim()
                val rawValue = parts[1].trim()

                if (rawValue.isEmpty()) {
                    // Assume it's a list starting from next lines
                    currentKey = key
                    currentList.clear()
                } else {
                    val value = rawValue.removeSurrounding("\"")
                    map[key] = value
                    currentKey = null
                }
            }
        }
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

