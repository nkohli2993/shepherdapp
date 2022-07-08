package com.app.shepherd.ui.component.addNewEvent

import com.google.gson.annotations.SerializedName

data class CreateEventModel(
    @SerializedName("loved_one_user_id") var loved_one_user_id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("assign_to") var assign_to: ArrayList<Int>? = null,
)
