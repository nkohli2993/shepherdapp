package com.shepherdapp.app.data.dto.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 12/07/22
 */
@Parcelize
data class UserRole(
    @SerializedName("role_id") var roleID: Int? = null
) : Parcelable
