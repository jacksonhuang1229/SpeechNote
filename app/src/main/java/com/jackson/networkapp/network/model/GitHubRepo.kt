package com.jackson.networkapp.network.model

import com.google.gson.annotations.SerializedName

/**
 * GitHub仓库模型类
 */
data class GitHubRepo(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("owner") val owner: Owner
)

/**
 * 仓库所有者模型类
 */
data class Owner(
    @SerializedName("id") val id: Long,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
) 