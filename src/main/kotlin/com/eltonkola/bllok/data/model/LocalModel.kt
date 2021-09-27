package com.eltonkola.bllok.data.model

import java.time.LocalDate


data class Article(
    val title: String,
    val content: String,
    val publicationDate: LocalDate,
    val updateDate: LocalDate,
    val author: Author,
    val label: List<Label>,
)

data class Author(
    val username: String,
    val avatarUrl: String,
    val githubLink: String,
)

data class Label(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val default: Boolean,
)

data class AppData(
    val labels: List<Label>,
    val articles : List<Article>,
    val config : Config,
)

data class Config(
    val postsPerPage: Int = 10,
    val twitter: String = "@eltonkola",
)
