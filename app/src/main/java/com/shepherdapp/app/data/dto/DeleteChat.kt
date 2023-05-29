package com.shepherdapp.app.data.dto

data class DeleteChat(
    val userId: Long? = 0L,
    val lovedOneId: Long? = 0L,
    var deletedAt: Long? = null,
)