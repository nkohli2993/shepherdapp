package com.app.shepherd.data.dto.dashboard

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class CareCondition (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : String? = null,
    @SerializedName("condition_id" ) var conditionId : Int?    = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null,
    @SerializedName("deleted_at"   ) var deletedAt   : String? = null

)
