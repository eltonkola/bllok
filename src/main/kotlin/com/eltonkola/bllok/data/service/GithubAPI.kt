package com.eltonkola.bllok.data.service


import com.eltonkola.bllok.data.model.GithubIssue
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GithubAPI {

    @GET("/repos/{owner}/{repo}/issues")
    fun getIssues(
        @Header("Authorization") accessToken: String,
        @Path("owner") owner: String,
        @Path("repo") repository: String
    ): List<GithubIssue>

}
