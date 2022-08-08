package com.shepherd.app.data.dto.med_list.loved_one_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 04/08/22
 */
@Parcelize
data class Medlist(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,

    @SerializedName("action_type") var actionType: Int? = null,
    @SerializedName("delete_position") var deletePosition: Int? = null,
):Parcelable
