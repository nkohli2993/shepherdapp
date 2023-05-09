package com.shepherdapp.app.data.dto.lock_box.lock_box_type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LockBoxAddedData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("user_id") var user_id: String? = null,
):Parcelable