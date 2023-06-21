package com.shepherdapp.app.data.dto.edit_event

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/12/22
 */
data class EditEventRequestModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("deleted_assignee") var deletedAssignee: ArrayList<String> = arrayListOf(),
    @SerializedName("new_assignee") var newAssignee: ArrayList<String> = arrayListOf(),
    @SerializedName("repeat_flag") var repeat_flag: String? = null,
    @SerializedName("repeat_end_date") var repeat_end_date: String? = null,
    @SerializedName("week_days") var week_days: ArrayList<Int>? = null,
    @SerializedName("month_dates") var month_dates: ArrayList<Int>?  = null,
    @SerializedName("year_dates") var year_dates: ArrayList<String>?  = null,
)
