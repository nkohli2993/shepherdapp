package com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 28/07/22
 */
@Parcelize
data class Payload(
    @SerializedName("document") var document: ArrayList<String> = arrayListOf()
):Parcelable
