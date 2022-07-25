package com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class LockBox(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("lbt_id") var lbtId: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("document_url") var documentUrl: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
