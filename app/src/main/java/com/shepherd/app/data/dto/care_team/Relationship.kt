package com.shepherd.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.relation.Relation
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 06/09/22
 */
@Parcelize
data class Relationship (

    @SerializedName("id"           ) var id         : Int?      = null,
    @SerializedName("user_id"      ) var userId     : String?   = null,
    @SerializedName("love_user_id" ) var loveUserId : String?   = null,
    @SerializedName("relation_id"  ) var relationId : Int?      = null,
    @SerializedName("created_at"   ) var createdAt  : String?   = null,
    @SerializedName("updated_at"   ) var updatedAt  : String?   = null,
    @SerializedName("deleted_at"   ) var deletedAt  : String?   = null,
    @SerializedName("relation"     ) var relation   : Relation? = Relation()
):Parcelable
