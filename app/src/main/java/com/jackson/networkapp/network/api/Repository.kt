package com.jackson.networkapp.network.api

import com.jackson.networkapp.network.di.NetworkModule
import com.jackson.networkapp.network.model.ApiResponse
import com.jackson.networkapp.network.model.GitHubRepo
import retrofit2.Response

/**
 * 数据仓库类
 * 负责处理所有的网络请求，对外提供数据访问接口
 */
class Repository {
    
    private val apiService = NetworkModule.apiService
    private val gitHubApiService = NetworkModule.gitHubApiService
    
    /**
     * 获取数据
     * @param page 页码
     * @return API响应
     */
    suspend fun getData(page: Int): Response<ApiResponse> {
        return apiService.getData(page)
    }
    
    /**
     * 获取GitHub用户的仓库列表
     * @param username GitHub用户名
     * @param page 页码
     * @param perPage 每页项目数
     * @return 仓库列表
     */
    suspend fun getUserRepositories(
        username: String,
        page: Int = 1,
        perPage: Int = 30
    ): Response<List<GitHubRepo>> {
        return gitHubApiService.getUserRepositories(username, page, perPage)
    }
    
    /**
     * 获取GitHub用户的星标仓库列表
     * @param username GitHub用户名
     * @param page 页码
     * @param perPage 每页项目数
     * @return 仓库列表
     */
    suspend fun getUserStarredRepositories(
        username: String,
        page: Int = 1,
        perPage: Int = 30
    ): Response<List<GitHubRepo>> {
        return gitHubApiService.getUserStarredRepositories(username, page, perPage)
    }
    
    companion object {
        // 单例模式
        @Volatile
        private var instance: Repository? = null
        
        fun getInstance(): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository().also { instance = it }
            }
        }
    }
} 