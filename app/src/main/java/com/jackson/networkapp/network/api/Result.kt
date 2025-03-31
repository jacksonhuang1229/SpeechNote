package com.jackson.networkapp.network.api

/**
 * 用于封装网络请求结果的密封类
 */
sealed class Result<out T> {
    /**
     * 成功状态，包含数据
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * 错误状态，包含错误信息
     */
    data class Error(val message: String, val code: Int = 0) : Result<Nothing>()
    
    /**
     * 加载中状态
     */
    object Loading : Result<Nothing>()
    
    /**
     * 检查是否成功
     */
    val isSuccess get() = this is Success
    
    /**
     * 检查是否失败
     */
    val isError get() = this is Error
    
    /**
     * 检查是否加载中
     */
    val isLoading get() = this == Loading
} 