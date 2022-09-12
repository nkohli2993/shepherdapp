package com.shepherdapp.app.data.dto.lock_box.upload_lock_box_doc

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 23/07/22
 */
@Parcelize
data class Payload(
    @SerializedName("document") var document: String? = null
):Parcelable
