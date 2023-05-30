package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class EnterPrisedata(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
) : Parcelable