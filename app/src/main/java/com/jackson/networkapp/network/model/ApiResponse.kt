package com.jackson.networkapp.network.model

import com.google.gson.annotations.SerializedName

/**
 * API响应的基本数据结构
 */
data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<DataItem>
)

/**
 * 数据项模型
 * 这只是一个示例，可以根据实际API响应修改
 */
data class DataItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
) 