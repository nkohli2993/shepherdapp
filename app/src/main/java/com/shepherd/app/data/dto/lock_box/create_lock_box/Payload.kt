package com.shepherd.app.data.dto.lock_box.create_lock_box

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class Payload(
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("lbt_id") var lbtId: Int? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("documents") var documents: ArrayList<Documents> = arrayListOf(),
    @SerializedName("user_id") var userId: String? = null
)
