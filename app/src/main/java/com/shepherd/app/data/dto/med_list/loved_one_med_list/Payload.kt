package com.shepherd.app.data.dto.med_list.loved_one_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 04/08/22
 */

@Parcelize
data class Payload(
    @SerializedName("user_medication_all") var userMedicationAll: ArrayList<UserMedicationData> = arrayListOf(),
    @SerializedName("user_medication_repeat") var userMedicationRepeat: ArrayList<UserMedicationRemiderData> = arrayListOf(),
) : Parcelable
