package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Config

class GithubRepository : DataRepository {

    //TODO - this will get the list of issues from github api
    override fun getArticles(page: Int): List<Article> {
        return emptyList()
    }

    override fun getConfig(): Config {
        return Config()
    }

}
