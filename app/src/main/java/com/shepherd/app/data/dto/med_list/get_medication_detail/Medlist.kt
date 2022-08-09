package com.shepherd.app.data.dto.med_list.get_medication_detail

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/08/22
 */


data class Medlist (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("slug") val slug : String,
	@SerializedName("description") val description : String,
	@SerializedName("is_active") val is_active : Boolean,
	@SerializedName("created_at") val created_at : String,
	@SerializedName("updated_at") val updated_at : String,
	@SerializedName("deleted_at") val deleted_at : String
)