package com.jackson.networkapp.network

import android.content.Context
import com.jackson.networkapp.network.di.NetworkModule

/**
 * 网络配置类
 * 用于初始化和配置网络模块
 */
object NetworkConfig {
    
    /**
     * 是否启用调试模式
     * 在调试模式下会输出详细的网络日志
     */
    var isDebug: Boolean = false
        private set
    
    /**
     * 初始化网络模块
     * @param context 应用上下文
     * @param debug 是否开启调试模式
     */
    fun init(context: Context, debug: Boolean = false) {
        isDebug = debug
    }
    
    /**
     * 获取API基础URL
     * 可以根据环境配置不同的URL
     */
    fun getBaseUrl(): String {
        return if (isDebug) {
            "https://dev-api.example.com/"
        } else {
            "https://api.example.com/"
        }
    }
    
    /**
     * 获取GitHub API基础URL
     */
    fun getGitHubBaseUrl(): String {
        return "https://api.github.com/"
    }
} 