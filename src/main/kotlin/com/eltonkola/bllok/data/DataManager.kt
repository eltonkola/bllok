package com.eltonkola.bllok.data

import com.eltonkola.bllok.data.model.AppData
import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.repository.DataRepository
import com.eltonkola.bllok.data.repository.GithubRepository
import com.eltonkola.bllok.data.repository.MockRepository
import java.lang.Exception

class DataManager {

    private val mockData : Boolean = true

    private val repo: DataRepository = if(mockData) MockRepository() else GithubRepository()

    fun loadAppData() : AppData {
        val config = repo.getConfig()

        var page = 1
        var loading = true
        var allArticles = mutableListOf<Article>()
        while(loading){
            try {
                val articles = repo.getArticles(page)
                if(articles.isEmpty()){
                    loading = false
                }else{
                    allArticles.addAll(articles)
                    page++
                }
            }catch (e: Exception){
                loading = false
            }

        }
        return AppData(
            articles = allArticles,
            config = config,
        )
    }

}

