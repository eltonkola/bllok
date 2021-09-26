package com.eltonkola.bllok.data.model

import java.util.*

data class Article(
    val title: String,
    val content: String,
    val publicationDate: Date,
    val updateDate: Date,
    val author: Author,
)

data class Author(
    val username: String,
    val avatarUrl: String,
    val githubLink: String,
)

data class AppData(
    val articles : List<Article>,
    val config : Config,
)

data class Config(
    val postsPerPage: Int = 10,
    val twitter: String = "@eltonkola",
)
