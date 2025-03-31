package com.jackson.speechnote

import android.app.Application
import com.jackson.speechnote.network.NetworkConfig

/**
 * 自定义Application类
 * 用于在应用启动时初始化各种组件
 */
class NetworkApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化网络模块
        initNetwork()
    }
    
    /**
     * 初始化网络模块
     */
    private fun initNetwork() {
        // 在调试版本中启用调试模式
        val isDebug = BuildConfig.DEBUG
        NetworkConfig.init(applicationContext, isDebug)
    }
} 