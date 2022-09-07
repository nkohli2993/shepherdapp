package com.shepherd.app.data.dto.lock_box.lock_box_type

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 22/07/22
 */
@Parcelize
data class LockBoxTypes(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("is_template") var isTemplate: Boolean? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("is_added") var isAdded: Boolean = false,
    @SerializedName("lockbox") var lockbox: ArrayList<LockBoxAddedData> = arrayListOf()
) : Parcelable
