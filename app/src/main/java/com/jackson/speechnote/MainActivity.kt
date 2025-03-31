package com.jackson.speechnote

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.jackson.speechnote.databinding.ActivityMainBinding
import com.jackson.speechnote.network.MainViewModel
import com.jackson.speechnote.network.api.Result

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 修改FAB点击事件，跳转到RepoListActivity
        binding.fab.setOnClickListener { view ->
            // 创建Intent并启动RepoListActivity
            val intent = Intent(this, RepoListActivity::class.java)
            startActivity(intent)
        }
        
        // 观察GitHub仓库数据
        observeGitHubRepos()
    }
    
    /**
     * 观察GitHub仓库数据变化
     */
    private fun observeGitHubRepos() {
        viewModel.githubReposResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val repoCount = result.data.size
                    Toast.makeText(this, "成功加载了 $repoCount 个仓库", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Toast.makeText(this, "加载失败: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                Result.Loading -> {
                    // 加载中状态已在按钮点击时处理
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}