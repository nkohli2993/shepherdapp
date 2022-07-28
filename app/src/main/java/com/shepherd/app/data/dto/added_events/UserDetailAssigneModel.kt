package com.shepherd.app.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class UserDetailAssigneModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("unique_uuid") var unique_uuid: String? = null,
    @SerializedName("is_active") var is_active: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("deleted_at") var deleted_at: String? = null,
    @SerializedName("user_profiles") var user_profiles: UserAssigneDetail = UserAssigneDetail(),

    )