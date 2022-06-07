package com.app.shepherd.network.retrofit

import com.app.shepherd.constants.ApiConstants
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneModel
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.app.shepherd.data.dto.add_loved_one.UploadPicResponseModel
import com.app.shepherd.data.dto.forgot_password.ForgotPasswordModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.relation.RelationResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Deepak Rattan on 27/05/22
 */
interface ApiService {

    @POST(ApiConstants.AUTHENTICATION.LOGIN)
    suspend fun login(@Body value: UserSignupData): Response<LoginResponseModel>

    @POST(ApiConstants.AUTHENTICATION.SIGN_UP)
    suspend fun signUp(@Body value: UserSignupData): Response<LoginResponseModel>

    @Multipart
    @POST(ApiConstants.AUTHENTICATION.UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Part profilePhoto: MultipartBody.Part?
    ): Response<UploadPicResponseModel>

    @POST(ApiConstants.AUTHENTICATION.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body value: ForgotPasswordModel): Response<LoginResponseModel>

    @GET(ApiConstants.RELATIONS.GET_RELATIONS)
    suspend fun getRelations(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<RelationResponseModel>

    @POST(ApiConstants.LOVED_ONE.CREATE_LOVED_ONE)
    suspend fun createLovedOne(@Body value: CreateLovedOneModel):Response<CreateLovedOneResponseModel>

}