package com.app.shepherd.network.retrofit

import com.app.shepherd.constants.ApiConstants
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneModel
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.app.shepherd.data.dto.add_loved_one.UploadPicResponseModel
import com.app.shepherd.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.app.shepherd.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.app.shepherd.data.dto.added_events.*
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberResponseModel
import com.app.shepherd.data.dto.dashboard.HomeResponseModel
import com.app.shepherd.data.dto.forgot_password.ForgotPasswordModel
import com.app.shepherd.data.dto.invitation.InvitationsResponseModel
import com.app.shepherd.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.app.shepherd.data.dto.medical_conditions.UserConditionsResponseModel
import com.app.shepherd.data.dto.relation.RelationResponseModel
import com.app.shepherd.data.dto.roles.RolesResponseModel
import com.app.shepherd.data.dto.signup.BioMetricData
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
import com.app.shepherd.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.app.shepherd.ui.base.BaseResponseModel
import com.app.shepherd.ui.component.addNewEvent.CreateEventModel
import com.app.shepherd.ui.component.addNewEvent.CreateEventResponseModel
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

    @GET(ApiConstants.CareTeams.GET_CARE_TEAM_ROLES)
    suspend fun getCareTeamRoles(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
//        @Query("status") status: Int
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.Authentication.USER_ROLES)
    suspend fun getUserRoles(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<RolesResponseModel>

    @GET(ApiConstants.UserDetails.GET_USER_DETAILS)
    suspend fun getUserDetails(
        @Path("id") id: Int
    ): Response<UserDetailsResponseModel>

    @GET(ApiConstants.UserDetails.GET_USER_DETAILS_BY_UUID)
    suspend fun getUserDetailByUUID(
        @Path("id") id: String
    ): Response<UserDetailByUUIDResponseModel>

    @POST(ApiConstants.Event.CREATE_EVENT)
    suspend fun createEvent(
        @Body value: CreateEventModel
    ): Response<CreateEventResponseModel>

    @GET(ApiConstants.Event.GET_EVENT)
    suspend fun getCreatedEvent(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("loved_one_user_id") loved_one_user_uid:String
    ): Response<AddedEventResponseModel>

    @GET(ApiConstants.Event.GET_EVENT_DETAIL)
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Response<EventDetailResponseModel>


    @POST(ApiConstants.MedicalConditions.CREATE_BULK_ONE_CONDITIONS)
    suspend fun createBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

    @GET(ApiConstants.Authentication.LOGOUT)
    suspend fun logout(): Response<BaseResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAMS)
    suspend fun getCareTeamsForLoggedInUser(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAMS)
    suspend fun getCareTeamsByLovedOneId(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int,
        @Query("loved_one_id") lovedOneUUID: String,
    ): Response<CareTeamsResponseModel>

    @GET(ApiConstants.CareTeams.GET_CARE_TEAMS)
    suspend fun getMembers(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int,
        @Query("loved_one_id") lovedOneId: String?
    ): Response<CareTeamsResponseModel>

    @POST(ApiConstants.CareTeams.ADD_NEW_CARE_TEAM_MEMBER)
    suspend fun addNewMemberCareTeam(@Body value: AddNewMemberCareTeamRequestModel): Response<AddNewMemberCareTeamResponseModel>

    @DELETE(ApiConstants.CareTeams.DELETE_CARE_TEAM_MEMBER)
    suspend fun deleteCareTeamMember(
        @Path("id") id: Int
    ): Response<DeleteCareTeamMemberResponseModel>

    @PUT(ApiConstants.CareTeams.UPDATE_CARE_TEAM_MEMBER)
    suspend fun updateCareTeamMember(
        @Path("id") id: Int,
        @Body updateCareTeamMemberRequestModel: UpdateCareTeamMemberRequestModel
    ): Response<UpdateCareTeamMemberResponseModel>

    @GET(ApiConstants.Home.GET_HOME_DATA)
    suspend fun getHomeData(
        @Query("love_user_id") lovedOneUUID: String,
        /*@Query("status") status: Int,*/
    ): Response<HomeResponseModel>

    @GET(ApiConstants.Invitations.GET_INVITATIONS)
    suspend fun getInvitations(
        @Query("sendType") sendType: String,
        @Query("status") status: Int
    ): Response<InvitationsResponseModel>

    @PATCH(ApiConstants.Invitations.ACCEPT_INVITATIONS)
    suspend fun acceptInvitation(@Path("id") id: Int): Response<AcceptInvitationResponseModel>


    @POST(ApiConstants.Event.ADD_EVENT_COMMENT)
    suspend fun createEventComment(
        @Body value: EventCommentModel
    ): Response<EventCommentResponseModel>

    @GET(ApiConstants.Event.GET_ALL_EVENT_COMMENT)
    suspend fun getEventComment(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("event_id") event_id: Int
    ): Response<AllCommentEventsResponseModel>

}