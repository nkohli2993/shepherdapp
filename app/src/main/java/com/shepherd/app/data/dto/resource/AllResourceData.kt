package com.shepherd.app.data.dto.resource

import com.google.gson.annotations.SerializedName

data class AllResourceData(
    @SerializedName("content")
    var content: String = "",
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
//    @SerializedName("post_tag")
//    var postTag: List<Any> = listOf(),
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_id")
    var userId: String = "",
)