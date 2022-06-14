package com.app.shepherd.network.retrofit

import com.app.shepherd.constants.ApiConstants
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneModel
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.app.shepherd.data.dto.add_loved_one.UploadPicResponseModel
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.forgot_password.ForgotPasswordModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.app.shepherd.data.dto.medical_conditions.UserConditionsResponseModel
import com.app.shepherd.data.dto.relation.RelationResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
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
    suspend fun createLovedOne(@Body value: CreateLovedOneModel): Response<CreateLovedOneResponseModel>

    @GET(ApiConstants.MEDICAL_CONDITIONS.GET_MEDICAL_CONDITIONS)
    suspend fun getMedicalConditions(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<MedicalConditionResponseModel>

    @GET(ApiConstants.CARE_TEAMS.GET_CARE_TEAMS)
    suspend fun getCareTeams(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.USER_DETAILS.GET_USER_DETAILS)
    suspend fun getUserDetails(
        @Path("id") id: Int
    ): Response<UserDetailsResponseModel>


    @POST(ApiConstants.MEDICAL_CONDITIONS.CREATE_BULK_ONE_CONDITIONS)
    suspend fun createBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

}