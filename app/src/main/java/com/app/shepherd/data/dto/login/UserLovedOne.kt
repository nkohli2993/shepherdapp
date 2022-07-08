package com.app.shepherd.data.dto.login

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 21/06/22
 */
data class UserLovedOne(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("role_id") var roleId: Int? = null,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(String::class.java.classLoader) as? String,
        parcel.readValue(String::class.java.classLoader) as? String,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(userId)
        parcel.writeValue(loveUserId)
        parcel.writeValue(roleId)
        parcel.writeValue(status)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(deletedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserLovedOne> {
        override fun createFromParcel(parcel: Parcel): UserLovedOne {
            return UserLovedOne(parcel)
        }

        override fun newArray(size: Int): Array<UserLovedOne?> {
            return arrayOfNulls(size)
        }
    }

}
