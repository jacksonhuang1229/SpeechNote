package com.jackson.speechnote.network.api

import retrofit2.Response
import java.io.IOException

/**
 * 安全地执行API调用并处理可能的异常
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                Result.Success(it)
            } ?: Result.Error("响应成功但内容为空")
        } else {
            val errorBody = response.errorBody()?.string() ?: "未知错误"
            Result.Error(errorBody, response.code())
        }
    } catch (e: IOException) {
        Result.Error("网络连接失败: ${e.message}")
    } catch (e: Exception) {
        Result.Error("请求失败: ${e.message}")
    }
} 