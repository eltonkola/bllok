package com.eltonkola.bllok.data.repository

import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.model.Config
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

    //TODO - this will get the list of issues from github api
    override fun getArticles(page: Int): List<Article> {
        return emptyList()
    }

    override fun getConfig(): Config {
        return Config()
    }

}
