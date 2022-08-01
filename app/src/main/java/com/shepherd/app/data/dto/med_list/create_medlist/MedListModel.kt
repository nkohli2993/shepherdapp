package com.shepherd.app.data.dto.med_list.create_medlist
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 01/08/22
 */
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
)