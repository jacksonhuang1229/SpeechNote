# NetworkApp

## 项目简介
NetworkApp是一个Android应用程序，展示了如何使用Retrofit和OkHttp构建现代化的网络请求模块。该应用主要功能是查询GitHub用户的公开仓库，并以列表形式展示。

## 主要功能
- 网络模块：基于Retrofit2和OkHttp实现的可配置网络层
- GitHub仓库查询：输入GitHub用户名，获取其公开仓库列表
- 数据展示：以列表形式展示仓库信息，包含仓库名称、描述和星标数

## 技术栈
- **语言**：Kotlin
- **架构**：MVVM (Model-View-ViewModel)
- **网络请求**：
  - Retrofit2：类型安全的HTTP客户端
  - OkHttp：高效的HTTP客户端
  - Gson：JSON序列化/反序列化
- **UI组件**：
  - DataBinding：声明式绑定UI组件
  - ViewBinding：类型安全地访问视图
- **并发处理**：Kotlin协程

## 项目结构
- **network模块**：
  - `api`：包含API接口定义和网络请求相关工具
  - `model`：包含数据模型类
  - `di`：包含依赖注入相关类
- **UI组件**：
  - `MainActivity`：应用入口
  - `RepoListActivity`：GitHub仓库列表查询界面

## 如何使用
1. 启动应用
2. 点击主界面右下角的搜索按钮
3. 在仓库列表界面输入GitHub用户名（例如：octocat）
4. 点击"获取 Repo List"按钮
5. 查看该用户的公开仓库列表

## 主要特点
- 模块化设计：网络层完全独立，易于扩展和维护
- 错误处理：提供友好的错误提示和加载状态
- 数据缓存：支持响应结果缓存
- 安全性：支持HTTPS和证书验证
- 调试功能：支持网络日志记录

## 学习资源
本项目展示了Android开发中的多种最佳实践，包括：
- 如何构建可维护的网络模块
- 如何使用MVVM架构组织代码
- 如何使用协程处理异步任务
- 如何实现UI与数据的分离