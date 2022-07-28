package com.shepherd.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 08/06/22
 */
@Parcelize
data class UserProfiles(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNumber: String? = null,
    @SerializedName("address") var address: String? = null
):Parcelable{
    val fullName = "$firstname $lastname"
}
