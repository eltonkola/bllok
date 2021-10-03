package com.eltonkola.bllok.data

import com.eltonkola.bllok.data.model.AppData
import com.eltonkola.bllok.data.model.Article
import com.eltonkola.bllok.data.repository.DataRepository
import com.eltonkola.bllok.data.repository.GithubRepository
import com.eltonkola.bllok.data.repository.MockRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class DataManager(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val mockData: Boolean = false

    private val repo: DataRepository = if (mockData) MockRepository() else GithubRepository()

    fun loadAppData(): AppData {

        return runBlocking(dispatcher) {

            val config = repo.getConfig()

            var page = 1
            var loading = true
            var allArticles = mutableListOf<Article>()
            while (loading) {
                try {
                    val articles = repo.getArticles(page)
                    if (articles.isEmpty()) {
                        loading = false
                    } else {
                        allArticles.addAll(articles)
                        page++
                    }
                } catch (e: Exception) {
                    loading = false
                    println("error: ${e.message}")
                }

            }
            println("allArticles: ${allArticles.size} pages: $page")

            AppData(
                articles = allArticles,
                config = config,
                labels = allArticles.flatMap { it.label }.distinct()
            )

        }


    }

}

