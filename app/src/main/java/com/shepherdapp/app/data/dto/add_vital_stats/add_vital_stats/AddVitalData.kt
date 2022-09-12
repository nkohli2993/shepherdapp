package com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 23/08/22
 */
@Parcelize
data class AddVitalData(
    @SerializedName("heart_rate") var heartRate: String? = null,
    @SerializedName("blood_pressure") var bloodPressure: AddBloodPressureData? = null,
    @SerializedName("sbp") var sbp: String? = null,
    @SerializedName("dbp") var dbp: String? = null,
    @SerializedName("body_temp") var bodyTemp: String? = null,
    @SerializedName("oxygen") var oxygen: String? = null
):Parcelable