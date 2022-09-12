package com.shepherdapp.app.data.dto.forgot_password

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 02/06/22
 */

@Parcelize
data class ForgotPasswordModel(
    @SerializedName("type") var type: String? = null,
    @SerializedName("email") var email: String? = null
):Parcelable
