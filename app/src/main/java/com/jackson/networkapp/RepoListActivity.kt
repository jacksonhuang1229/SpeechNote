package com.jackson.networkapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jackson.networkapp.databinding.ActivityRepoListBinding
import com.jackson.networkapp.network.MainViewModel
import com.jackson.networkapp.network.api.Result
import com.jackson.networkapp.network.model.GitHubRepo

class RepoListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRepoListBinding
    private lateinit var viewModel: MainViewModel
    private val TAG = "RepoListActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用ViewBinding
        binding = ActivityRepoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置标题
        title = "GitHub仓库列表"
        
        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        // 设置获取仓库按钮点击事件
        binding.btnGetRepos.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "请输入GitHub用户名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 显示加载中
            binding.progressBar.visibility = View.VISIBLE
            binding.tvNoRepos.visibility = View.GONE
            
            // 加载GitHub仓库
            viewModel.loadGitHubRepositories(username)
        }
        
        // 观察GitHub仓库数据
        observeGitHubRepos()
    }
    
    /**
     * 观察GitHub仓库数据变化
     */
    private fun observeGitHubRepos() {
        viewModel.githubReposResult.observe(this) { result ->
            // 隐藏加载中
            binding.progressBar.visibility = View.GONE
            
            when (result) {
                is Result.Success -> {
                    val repos = result.data
                    Log.d(TAG, "成功获取到 ${repos.size} 个仓库")
                    
                    if (repos.isEmpty()) {
                        binding.tvNoRepos.visibility = View.VISIBLE
                        binding.tvNoRepos.text = "该用户没有公开的仓库"
                        binding.listViewRepos.visibility = View.GONE
                    } else {
                        binding.tvNoRepos.visibility = View.GONE
                        binding.listViewRepos.visibility = View.VISIBLE
                        displayRepos(repos)
                    }
                }
                is Result.Error -> {
                    Log.e(TAG, "获取仓库失败: ${result.message}")
                    binding.tvNoRepos.visibility = View.VISIBLE
                    binding.tvNoRepos.text = "获取仓库失败: ${result.message}"
                    binding.listViewRepos.visibility = View.GONE
                }
                Result.Loading -> {
                    Log.d(TAG, "正在加载仓库...")
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
    
    /**
     * 显示仓库列表
     */
    private fun displayRepos(repos: List<GitHubRepo>) {
        // 创建仓库显示名称列表
        val repoNames = repos.map { repo -> 
            "${repo.name} (⭐${repo.stars})\n${repo.description ?: "无描述"}"
        }
        
        // 使用ArrayAdapter显示仓库列表
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            repoNames
        )
        
        binding.listViewRepos.adapter = adapter
    }
} 