package com.shepherdapp.app.data.dto.med_list.add_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 13/09/22
 */
@Parcelize
data class AddMedListRequestModel(

    @SerializedName("name") var condition: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("created_by") var created_by: String? = null
) : Parcelable
