package com.shepherd.app.data.dto.settings_pages
import com.google.gson.annotations.SerializedName


data class Payload(
    @SerializedName("about")
    var about: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("privacy_policy")
    var privacyPolicy: String = "",
    @SerializedName("terms_and_conditons")
    var termsAndConditons: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""
)