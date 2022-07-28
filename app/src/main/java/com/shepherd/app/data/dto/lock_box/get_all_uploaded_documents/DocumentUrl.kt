package com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 28/07/22
 */
@Parcelize
data class DocumentUrl(
    @SerializedName("url") var url: String? = null,
) : Parcelable
