package com.shepherd.app.data.dto.add_new_member_care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/06/22
 */
data class Payload(
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("receiver_user_id") var receiverUserId: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("loveone_user_id") var loveoneUserId: String? = null,
    @SerializedName("careteam_role_id") var careteamRoleId: Int? = null,
    @SerializedName("uuid") var uuid: String? = null,
    @SerializedName("permission") var permission: String? = null
)
