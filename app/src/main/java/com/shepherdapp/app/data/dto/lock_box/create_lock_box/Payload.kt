package com.shepherdapp.app.data.dto.lock_box.create_lock_box

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 25/07/22
 */
@Parcelize
data class Payload(
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("lbt_id") var lbtId: Int? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("documents") var documents: ArrayList<Documents>? = arrayListOf(),
    @SerializedName("allowed_user_ids") var allowedUserIds: ArrayList<String>? = arrayListOf(),
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("lockbox_types") var lockbox_types: LockBoxTypes = LockBoxTypes(),
    @SerializedName("allowed_users") var allowedUsers: ArrayList<AllowedUsers> = arrayListOf()
) : Parcelable
