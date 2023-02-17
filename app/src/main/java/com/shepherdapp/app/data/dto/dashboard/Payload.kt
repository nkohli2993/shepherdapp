package com.shepherdapp.app.data.dto.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 29/06/22
 */
@Parcelize
data class Payload(
    @SerializedName("carePoints") var carePoints: Int? = null,
    @SerializedName("medLists") var medLists: Int? = null,
    @SerializedName("lockBoxs") var lockBoxs: Int? = null,
    @SerializedName("careTeams") var careTeams: Int? = null,
    @SerializedName("unreadNotificationsCount") var unreadNotificationsCount: Int? = null,
    @SerializedName("lovedOneUserProfile") var lovedOneUserProfile: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("careTeamProfiles") var careTeamProfiles: ArrayList<CareTeamProfiles> = arrayListOf()
) : Parcelable
