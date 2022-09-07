package com.shepherd.app.data.dto.lock_box.lock_box_type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class LockBoxAddedData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
):Parcelable