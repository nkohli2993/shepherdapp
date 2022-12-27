package com.shepherdapp.app.data.dto.resource

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllResourceData(
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("count")
    var count: Int? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("deleted_at")
    var deletedAt: String? = null,
    @SerializedName("id")
    var id: Int? = null,
//    @SerializedName("post_tag")
//    var postTag: List<Any> = listOf(),
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("post_tag")
    var postTag: ArrayList<PostTag> = arrayListOf(),
    @SerializedName("category")
    var category: Category? = Category()

) : Parcelable