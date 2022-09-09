package com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 25/07/22
 */
@Parcelize
data class Payload(
    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("lock_box") var lockBox: ArrayList<LockBox> = arrayListOf()
):Parcelable
