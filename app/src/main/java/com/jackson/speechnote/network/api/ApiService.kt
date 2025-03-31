package com.jackson.speechnote.network.api

import com.jackson.speechnote.network.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API服务接口
 * 定义与后端交互的API方法
 */
interface ApiService {
    
    /**
     * 示例API：获取数据列表
     * @param page 页码
     * @return API响应
     */
    @GET("data")
    suspend fun getData(@Query("page") page: Int): Response<ApiResponse>
    
    // 这里可以添加更多API端点
} 