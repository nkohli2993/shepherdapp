package com.shepherd.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 02/09/22
 */
@Parcelize
data class CreatedByDetails(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("uid") var uid: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("address") var address: String? = null
) : Parcelable
