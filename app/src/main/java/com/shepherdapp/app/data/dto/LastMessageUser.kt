package com.shepherdapp.app.data.dto

class LastMessageUser : java.io.Serializable {
    val createdAt: Long? = null
    val recieverId: Long? = null
    val senderId: Long? = null
    val text: String? = null
    val video: VideoMessageData? = null
    val imageUrl: String? = null


    //Add this
    constructor() {}

    fun manageCreatedAt(): Long {
        return if (createdAt.toString().length == 10)
            createdAt!! * 1000
        else
            createdAt!!
    }


}
