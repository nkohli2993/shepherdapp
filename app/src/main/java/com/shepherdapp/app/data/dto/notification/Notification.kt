package com.shepherdapp.app.data.dto.notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 20/09/22
 */

data class Notification(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("model_type") var modelType: String? = null,
    @SerializedName("model_id") var modelId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
