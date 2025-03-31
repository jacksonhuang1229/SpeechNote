package com.jackson.speechnote.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jackson.speechnote.network.api.Repository
import com.jackson.speechnote.network.api.Result
import com.jackson.speechnote.network.api.safeApiCall
import com.jackson.speechnote.network.model.ApiResponse
import com.jackson.speechnote.network.model.GitHubRepo
import kotlinx.coroutines.launch

/**
 * 主ViewModel类
 * 负责处理UI相关的数据流和业务逻辑
 */
class MainViewModel : ViewModel() {
    
    private val repository = Repository.getInstance()
    private val TAG = "MainViewModel"
    
    private val _dataResult = MutableLiveData<Result<ApiResponse>>()
    val dataResult: LiveData<Result<ApiResponse>> = _dataResult
    
    private val _githubReposResult = MutableLiveData<Result<List<GitHubRepo>>>()
    val githubReposResult: LiveData<Result<List<GitHubRepo>>> = _githubReposResult
    
    /**
     * 加载数据
     * @param page 页码
     */
    fun loadData(page: Int = 1) {
        viewModelScope.launch {
            _dataResult.value = Result.Loading
            val result = safeApiCall { repository.getData(page) }
            _dataResult.value = result
        }
    }
    
    /**
     * 加载GitHub用户的仓库列表
     * @param username GitHub用户名
     */
    fun loadGitHubRepositories(username: String) {
        viewModelScope.launch {
            _githubReposResult.value = Result.Loading
            
            val result = safeApiCall { repository.getUserRepositories(username) }
            _githubReposResult.value = result
            
            // 打印结果
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "成功获取到 ${result.data.size} 个仓库")
                    result.data.forEachIndexed { index, repo ->
                        Log.d(TAG, "${index + 1}. ${repo.name}: ${repo.description ?: "无描述"}")
                        Log.d(TAG, "   URL: ${repo.htmlUrl}")
                        Log.d(TAG, "   语言: ${repo.language ?: "未指定"}, Stars: ${repo.stars}, Forks: ${repo.forks}")
                        Log.d(TAG, "   所有者: ${repo.owner.login}")
                        Log.d(TAG, "-----------------------------------")
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "获取仓库失败: ${result.message}")
                }
                Result.Loading -> {
                    Log.d(TAG, "正在加载仓库...")
                }
            }
        }
    }
} 