package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Config

interface DataRepository {
    suspend fun getArticles(page: Int) : List<Article>
    suspend fun getConfig() : Config
}
