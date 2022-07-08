package com.app.shepherd.data.dto.care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/07/22
 */
data class PayloadUpdateCareTeam(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("love_user_id") var loveUserId: Int? = null,
    @SerializedName("role_id") var roleId: Int? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
