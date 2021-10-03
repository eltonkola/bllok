package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.Bllok
import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Config
import com.eltonkola.bllok.data.model.toArticle
import com.eltonkola.bllok.data.service.GithubAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class GithubRepository : DataRepository {

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: GithubAPI = retrofit.create(GithubAPI::class.java)

    override suspend fun getArticles(page: Int): List<Article> {
        val issues = service.getIssues(Bllok.githubToken, Bllok.owner, Bllok.repo, page, 100)
        println("issues: ${issues.size} pages: $page")
        return issues.map {
             it.toArticle()
        }
    }

    override suspend fun getConfig(): Config {
        return Config()
    }

}
