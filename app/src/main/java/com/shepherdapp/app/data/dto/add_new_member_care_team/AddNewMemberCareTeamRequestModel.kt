package com.shepherdapp.app.data.dto.add_new_member_care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 27/06/22
 */
@Parcelize
data class AddNewMemberCareTeamRequestModel(

    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("receiver_user_id") var receiverUserId: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("loveone_user_id") var loveoneUserId: String? = null,
    @SerializedName("careteam_role_id") var careteamRoleId: Int? = null,
    @SerializedName("permission") var permission: String? = null
)
    :Parcelable