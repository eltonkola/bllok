package com.eltonkola.bllok.data.model

import java.time.ZonedDateTime


data class Article(
    val id : Int,
    val title: String,
    val content: String,
    val publicationDate: ZonedDateTime,
    val updateDate: ZonedDateTime,
    val author: Author,
    val label: List<Label>,
)

data class Author(
    val username: String,
    val avatarUrl: String,
    val githubLink: String,
)

data class Label(
    val id: String,
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
    val twitter: String = "http://twitter.com/eltonkola",
    val github: String = "http://github.com/eltonkola",
    val webUrl: String = "https://eltonkola.github.io/bllok/",
    val websiteName: String = "Bllok",
    val websiteDescription: String = "another static blog",
    val copyright: String = "2021 eKola",
    val feedEmail: String = "eltonkola@gmail.com",
    val feedEmailRealName: String = "Elton Kola"
)
