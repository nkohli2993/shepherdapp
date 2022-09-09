package com.shepherd.app.data.dto.med_list.schedule_medlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 07/09/22
 */
@Parcelize
data class TimePickerData(
    @SerializedName("position") var position: Int? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("value") var value:String? = null
):Parcelable
