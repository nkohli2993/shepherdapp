package com.shepherdapp.app.data.dto.push_notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/09/22
 */

data class FCMResponseModel (

    @SerializedName("multicast_id"  ) var multicastId  : Long?               = null,
    @SerializedName("success"       ) var success      : Int?               = null,
    @SerializedName("failure"       ) var failure      : Int?               = null,
    @SerializedName("canonical_ids" ) var canonicalIds : Int?               = null,
    @SerializedName("results"       ) var results      : ArrayList<Result> = arrayListOf()

)
