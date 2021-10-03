package com.eltonkola.bllok.data.service


import com.eltonkola.bllok.data.model.GithubIssue
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubAPI {

    @GET("/repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Header("Authorization") accessToken: String,
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int, //default 30, max 100
        @Query("state") state: String = "closed", //open, closed, open (default)
    ): List<GithubIssue>

}
