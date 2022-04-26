package com.app.shepherd.usecase.errors

import com.app.shepherd.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
