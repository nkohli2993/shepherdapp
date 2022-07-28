package com.shepherd.app.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class ResultEventModel(
    @SerializedName("date") var date: String? = null,
    @SerializedName("events") var events: ArrayList<AddedEventModel> = arrayListOf()
)