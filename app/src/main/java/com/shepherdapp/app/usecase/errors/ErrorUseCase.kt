package com.shepherdapp.app.usecase.errors

import com.shepherdapp.app.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
