package com.shepherd.app.data.dto.add_vital_stats.add_vital_stats
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 01/09/22
 */
@Parcelize
data class AddBloodPressureData(
    @SerializedName("sbp") var sbp: String? = null,
    @SerializedName("dbp") var dbp: String? = null,
):Parcelable