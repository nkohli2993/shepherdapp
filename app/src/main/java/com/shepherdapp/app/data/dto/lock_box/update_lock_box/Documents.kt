package com.shepherdapp.app.data.dto.lock_box.update_lock_box

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 28/07/22
 */
@Parcelize
data class Documents(
    @SerializedName("url") var url: String? = null
):Parcelable
