package com.shepherdapp.app.data.dto.dashboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 12/07/22
 */
@Parcelize
data class CareCondition (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : String? = null,
    @SerializedName("condition_id" ) var conditionId : Int?    = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null,
    @SerializedName("deleted_at"   ) var deletedAt   : String? = null

):Parcelable
