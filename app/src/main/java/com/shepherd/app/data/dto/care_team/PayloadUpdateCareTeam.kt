package com.shepherd.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 04/07/22
 */
@Parcelize
data class PayloadUpdateCareTeam(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("role_id") var roleId: Int? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
):Parcelable
