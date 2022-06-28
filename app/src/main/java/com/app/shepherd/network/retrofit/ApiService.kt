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
import com.app.shepherd.data.dto.signup.BioMetricData
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
import com.app.shepherd.ui.component.addNewEvent.CreateEventModel
import com.app.shepherd.ui.component.addNewEvent.CreateEventResponseModel
import com.app.shepherd.ui.base.BaseResponseModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Deepak Rattan on 27/05/22
 */
interface ApiService {

    @POST(ApiConstants.Authentication.LOGIN)
    suspend fun login(@Body value: UserSignupData): Response<LoginResponseModel>

    @POST(ApiConstants.Authentication.LOGIN_WITH_DEVICE)
    suspend fun loginWithDevice(@Body value: UserSignupData): Response<LoginResponseModel>

    @POST(ApiConstants.Authentication.SIGN_UP)
    suspend fun signUp(@Body value: UserSignupData): Response<LoginResponseModel>

    @PATCH(ApiConstants.Authentication.BIOMETRIC)
    suspend fun registerBioMetric(@Body value: BioMetricData): Response<LoginResponseModel>

    @Multipart
    @POST(ApiConstants.Authentication.UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Part profilePhoto: MultipartBody.Part?
    ): Response<UploadPicResponseModel>

    @POST(ApiConstants.Authentication.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body value: ForgotPasswordModel): Response<LoginResponseModel>

    @GET(ApiConstants.Relations.GET_RELATIONS)
    suspend fun getRelations(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<RelationResponseModel>

    @POST(ApiConstants.LovedOne.CREATE_LOVED_ONE)
    suspend fun createLovedOne(@Body value: CreateLovedOneModel): Response<CreateLovedOneResponseModel>

    @GET(ApiConstants.MedicalConditions.GET_MEDICAL_CONDITIONS)
    suspend fun getMedicalConditions(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<MedicalConditionResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAMS)
    suspend fun getCareTeams(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAM_ROLES)
    suspend fun getCareTeamRoles(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.UserDetails.GET_USER_DETAILS)
    suspend fun getUserDetails(
        @Path("id") id: Int
    ): Response<UserDetailsResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAMS)
    suspend fun getMembers(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int,
        @Query("loved_one_id") lovedOneId: Int
    ): Response<CareTeamsResponseModel>

    @POST(ApiConstants.CreateEvent.CREATE_EVENT)
    suspend fun createEvent(
        @Body value: CreateEventModel
    ): Response<CreateEventResponseModel>


    @POST(ApiConstants.MedicalConditions.CREATE_BULK_ONE_CONDITIONS)
    suspend fun createBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

    @GET(ApiConstants.Authentication.LOGOUT)
    suspend fun logout(): Response<BaseResponseModel>
}