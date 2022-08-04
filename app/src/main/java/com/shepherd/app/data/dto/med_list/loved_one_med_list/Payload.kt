package com.shepherd.app.data.dto.med_list.loved_one_med_list

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/08/22
 */
data class Payload(
    @SerializedName("medlists") var medlists: ArrayList<Medlists> = arrayListOf()
)
