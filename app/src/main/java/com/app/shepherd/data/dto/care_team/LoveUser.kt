package com.app.shepherd.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 08/06/22
 */
@Parcelize
data class LoveUser(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles()

) : Parcelable
