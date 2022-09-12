package com.shepherdapp.app.data.dto.invitation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 05/07/22
 */
@Parcelize
data class User(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles()
):Parcelable
