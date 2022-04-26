package com.app.shepherd.usecase.errors

import com.app.shepherd.data.error.Error
import com.app.shepherd.data.error.mapper.ErrorMapper
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */

class ErrorManager @Inject constructor(private val errorMapper: ErrorMapper) : ErrorUseCase {
    override fun getError(errorCode: Int): Error {
        return Error(code = errorCode, description = errorMapper.errorsMap.getValue(errorCode))
    }
}
