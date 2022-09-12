package com.shepherdapp.app.data.dto.edit_profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 17/058/22
 */
@Parcelize
data class UserUpdateData(

    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("device") var device: String? = null,

    ):Parcelable