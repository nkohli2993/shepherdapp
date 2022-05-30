package com.app.shepherd.network.retrofit

import com.app.shepherd.constants.ApiConstants
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Deepak Rattan on 27/05/22
 */
interface ApiService {

    @POST(ApiConstants.AUTHENTICATION.LOGIN)
    suspend fun login(@Body value: UserSignupData): Response<LoginResponseModel>

}