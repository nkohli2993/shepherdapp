package com.shepherd.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Created by Deepak Rattan on 27/05/22
 */
@Parcelize
data class UserRoles (

    @SerializedName("role_id" ) var roleId : Int? = null

):Parcelable