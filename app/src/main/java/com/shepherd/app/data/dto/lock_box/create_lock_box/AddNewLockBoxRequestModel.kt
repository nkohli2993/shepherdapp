package com.shepherd.app.data.dto.lock_box.create_lock_box

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */

data class AddNewLockBoxRequestModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("lbt_id") var lbtId: Int? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
//    @SerializedName("document_url") var documentUrl: String? = null
    @SerializedName("documents") var documents: ArrayList<Documents> = arrayListOf()
)