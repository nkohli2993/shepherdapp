package com.shepherdapp.app.data.dto.lock_box.edit_lock_box

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.Documents

/**
 * Created by Nikita Kohli on 12/09/22
 */

data class EditLockBoxRequestModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("lbt_id") var lbtId: Int? = null,
    @SerializedName("documents") var documents: ArrayList<Documents>? = null,
    @SerializedName("deleted_documents") var deletedDocuments: ArrayList<Documents>? = null,
    @SerializedName("allowed_user_ids") var allowedUserIds: ArrayList<String>? = arrayListOf()
)