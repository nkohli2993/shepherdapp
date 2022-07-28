package com.shepherd.app.usecase.errors

import com.shepherd.app.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
