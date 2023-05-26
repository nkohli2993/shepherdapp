package com.shepherdapp.app.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli
 */
@Parcelize
data class WeekDataModel(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("is_selected")
    var isSelected :Boolean = false
) : Parcelable