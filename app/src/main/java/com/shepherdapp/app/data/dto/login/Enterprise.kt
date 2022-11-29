package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 29/11/22
 */
@Parcelize
data class Enterprise(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null
) : Parcelable
