package com.app.shepherd.network.retrofit

sealed class DataResult<out T> {
    data class Loading(val nothing: Nothing? = null) : DataResult<Nothing>()
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Failure(val message: String? = null, val errorCode: Int? = null) :
        DataResult<Nothing>()
}