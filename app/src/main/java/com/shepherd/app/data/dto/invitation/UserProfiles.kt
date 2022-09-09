package com.shepherd.app.data.dto.invitation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 05/07/22
 */
@Parcelize
data class UserProfiles (

    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("user_id"       ) var userId       : Int?    = null,
    @SerializedName("firstname"     ) var firstname    : String? = null,
    @SerializedName("lastname"      ) var lastname     : String? = null,
    @SerializedName("profile_photo" ) var profilePhoto : String? = null
):Parcelable
