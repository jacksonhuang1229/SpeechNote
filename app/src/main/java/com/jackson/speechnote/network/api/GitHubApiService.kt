package com.jackson.speechnote.network.api

import com.jackson.speechnote.network.model.GitHubRepo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * GitHub API服务接口
 * 定义与GitHub API交互的方法
 */
interface GitHubApiService {
    
    /**
     * 获取用户的仓库列表
     * @param username GitHub用户名
     * @param page 页码
     * @param perPage 每页项目数
     * @return 仓库列表
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<GitHubRepo>>
    
    /**
     * 获取用户的星标仓库列表
     * @param username GitHub用户名
     * @param page 页码
     * @param perPage 每页项目数
     * @return 仓库列表
     */
    @GET("users/{username}/starred")
    suspend fun getUserStarredRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<GitHubRepo>>
} 