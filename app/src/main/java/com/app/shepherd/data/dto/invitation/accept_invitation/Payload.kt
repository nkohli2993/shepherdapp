package com.app.shepherd.data.dto.invitation.accept_invitation

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 11/07/22
 */
data class Payload(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("receiver_user_id") var receiverUserId: String? = null,
    @SerializedName("loveone_user_id") var loveoneUserId: String? = null,
    @SerializedName("careteam_role_id") var careteamRoleId: Int? = null,
    @SerializedName("uuid") var uuid: String? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)
