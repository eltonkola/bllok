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
    val copyright: String,
    val language: String
)

fun parseTomlConfig(filePath: String): BlogConfig {
    val map = mutableMapOf<String, String>()

    val lines = File(filePath).readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }

    for (line in lines) {
        val keyValue = line.split("=", limit = 2)
        if (keyValue.size == 2) {
            val key = keyValue[0].trim()
            val rawValue = keyValue[1].trim()
            val value = when {
                rawValue.startsWith("[") && rawValue.endsWith("]") -> {
                    rawValue.removeSurrounding("[", "]")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                        .joinToString(",") { it }
                }
                rawValue.startsWith("\"") -> rawValue.removeSurrounding("\"")
                else -> rawValue
            }
            map[key] = value
        }
    }

    return BlogConfig(
        postsPerPage = map["postsPerPage"]?.toIntOrNull() ?: 10,
        baseUrl = map["baseUrl"] ?: "",
        websiteName = map["websiteName"] ?: "",
        websiteDescription = map["websiteDescription"] ?: "",
        feedEmail = map["feedEmail"] ?: "",
        feedEmailRealName = map["feedEmailRealName"] ?: "",
        socials = map["socials"]?.split(",")?.map { it.trim().removeSurrounding("\"") } ?: emptyList(),
        copyright = map["copyright"] ?: "",
        language = map["language"] ?: "en"
    )
}
