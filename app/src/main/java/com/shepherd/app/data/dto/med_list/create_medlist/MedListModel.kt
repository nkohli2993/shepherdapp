package com.shepherd.app.data.dto.med_list.create_medlist
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 01/08/22
 */
@Parcelize
data class MedListModel(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_active")
    var isActive: Boolean = false,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""
):Parcelable