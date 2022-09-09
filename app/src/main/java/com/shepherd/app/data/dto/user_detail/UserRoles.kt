package com.shepherd.app.data.dto.user_detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 12/07/22
 */
@Parcelize
data class UserRoles(
    @SerializedName("role_id") var roleId: Int? = null
):Parcelable
