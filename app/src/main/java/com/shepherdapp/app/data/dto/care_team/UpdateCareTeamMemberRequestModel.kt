package com.shepherdapp.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 04/07/22
 */
@Parcelize
data class UpdateCareTeamMemberRequestModel(
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("relation_name") var relation_name: String? = null
):Parcelable
