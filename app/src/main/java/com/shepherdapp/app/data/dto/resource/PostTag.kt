package com.shepherdapp.app.data.dto.resource

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 28/10/22
 */
@Parcelize
data class PostTag(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("post_id") var postId: Int? = null,
    @SerializedName("tag_id") var tagId: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("tag") var tag: Tag? = Tag()
) : Parcelable
