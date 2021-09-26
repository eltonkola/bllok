package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Config

interface DataRepository {
    fun getArticles(page: Int) : List<Article>
    fun getConfig() : Config
}
