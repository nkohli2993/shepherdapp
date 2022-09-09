package com.shepherd.app.data.dto.add_vital_stats.add_vital_stats

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.care_team.CareTeam
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.care_team.CareTeamRoles
import kotlinx.android.parcel.Parcelize


/**
 * Created by Nikita on 23/08/22
 */
@Parcelize
data class Payload(

    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("loveone_user_id") var loveone_user_id: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("data") var data: AddVitalData? = null

):Parcelable
