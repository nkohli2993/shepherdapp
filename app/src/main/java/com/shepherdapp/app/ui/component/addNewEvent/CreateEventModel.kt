package com.shepherdapp.app.ui.component.addNewEvent

import com.google.gson.annotations.SerializedName

data class CreateEventModel(
    @SerializedName("loved_one_user_id") var loved_one_user_id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("assign_to") var assign_to: ArrayList<String>? = null,
    @SerializedName("repeat_flag") var repeat_flag: String? = null,
    @SerializedName("repeat_end_date") var repeat_end_date: String? = null,
    @SerializedName("week_days") var week_days: ArrayList<Int>? = null,
    @SerializedName("month_dates") var month_dates: ArrayList<Int>?  = null,
    @SerializedName("year_dates") var year_dates: ArrayList<String>?  = null,
)
