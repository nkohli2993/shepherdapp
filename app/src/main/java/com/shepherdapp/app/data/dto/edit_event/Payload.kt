package com.shepherdapp.app.data.dto.edit_event

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/12/22
 */
data class Payload(
    @SerializedName("id"                ) var id             : Int?                    = null,
    @SerializedName("loved_one_user_id" ) var lovedOneUserId : String?                 = null,
    @SerializedName("created_by"        ) var createdBy      : String?                 = null,
    @SerializedName("name"              ) var name           : String?                 = null,
    @SerializedName("location"          ) var location       : String?                 = null,
    @SerializedName("date"              ) var date           : String?                 = null,
    @SerializedName("time"              ) var time           : String?                 = null,
    @SerializedName("notes"             ) var notes          : String?                 = null,
    @SerializedName("is_active"         ) var isActive       : Boolean?                = null,
    @SerializedName("created_at"        ) var createdAt      : String?                 = null,
    @SerializedName("updated_at"        ) var updatedAt      : String?                 = null,
    @SerializedName("deleted_at"        ) var deletedAt      : String?                 = null,
    @SerializedName("user_assignes"     ) var userAssignes   : ArrayList<UserAssignes> = arrayListOf(),
    @SerializedName("event_comments"    ) var eventComments  : ArrayList<String>       = arrayListOf()
)
