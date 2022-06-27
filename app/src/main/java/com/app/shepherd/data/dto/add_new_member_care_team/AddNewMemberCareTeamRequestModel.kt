package com.app.shepherd.data.dto.add_new_member_care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/06/22
 */

data class AddNewMemberCareTeamRequestModel(

    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("receiver_user_id") var receiverUserId: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("loveone_user_id") var loveoneUserId: Int? = null,
    @SerializedName("careteam_role_id") var careteamRoleId: Int? = null,
    @SerializedName("permission") var permission: String? = null
)
