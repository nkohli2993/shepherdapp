package com.shepherd.app.network.retrofit

import com.shepherd.app.constants.ApiConstants
import com.shepherd.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherd.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherd.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherd.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherd.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherd.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherd.app.data.dto.add_vital_stats.VitalStatsResponseModel
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherd.app.data.dto.added_events.*
import com.shepherd.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherd.app.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.shepherd.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherd.app.data.dto.care_team.UpdateCareTeamMemberResponseModel
import com.shepherd.app.data.dto.change_password.ChangePasswordModel
import com.shepherd.app.data.dto.dashboard.HomeResponseModel
import com.shepherd.app.data.dto.edit_loved_one.EditLovedOneResponseModel
import com.shepherd.app.data.dto.edit_profile.UserUpdateData
import com.shepherd.app.data.dto.forgot_password.ForgotPasswordModel
import com.shepherd.app.data.dto.invitation.InvitationsResponseModel
import com.shepherd.app.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherd.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxResponseModel
import com.shepherd.app.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc.UploadMultipleLockBoxDoxResponseModel
import com.shepherd.app.data.dto.login.EditResponseModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.dto.med_list.*
import com.shepherd.app.data.dto.med_list.get_medication_detail.GetMedicationDetailResponse
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordResponseModel
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionResponseModel
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.data.dto.medical_conditions.UpdateMedicalConditionRequestModel
import com.shepherd.app.data.dto.medical_conditions.UserConditionsResponseModel
import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherd.app.data.dto.relation.RelationResponseModel
import com.shepherd.app.data.dto.resource.ParticularResourceResponseModel
import com.shepherd.app.data.dto.resource.ResponseRelationModel
import com.shepherd.app.data.dto.roles.RolesResponseModel
import com.shepherd.app.data.dto.security_code.SecurityCodeResponseModel
import com.shepherd.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherd.app.data.dto.settings_pages.StaticPageResponseModel
import com.shepherd.app.data.dto.signup.BioMetricData
import com.shepherd.app.data.dto.signup.UserSignupData
import com.shepherd.app.data.dto.user.UserDetailsResponseModel
import com.shepherd.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherd.app.ui.base.BaseResponseModel
import com.shepherd.app.ui.component.addNewEvent.CreateEventModel
import com.shepherd.app.ui.component.addNewEvent.CreateEventResponseModel
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

    @POST(ApiConstants.Authentication.CHANGE_PASSWORD)
    suspend fun changePassword(@Body value: ChangePasswordModel): Response<BaseResponseModel>

    @GET(ApiConstants.Relations.GET_RELATIONS)
    suspend fun getRelations(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<RelationResponseModel>

    @POST(ApiConstants.LovedOne.CREATE_LOVED_ONE)
    suspend fun createLovedOne(
        @Body value: CreateLovedOneModel
    ): Response<CreateLovedOneResponseModel>

    @PUT(ApiConstants.LovedOne.EDIT_LOVED_ONE)
    suspend fun editLovedOne(
        @Path("id") id: Int?,
        @Body value: CreateLovedOneModel
    ): Response<EditLovedOneResponseModel>

    @GET(ApiConstants.MedicalConditions.GET_MEDICAL_CONDITIONS)
    suspend fun getMedicalConditions(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<MedicalConditionResponseModel>

    @GET(ApiConstants.MedicalConditions.GET_LOVED_ONE_MEDICAL_CONDITION)
    suspend fun getLovedOneMedicalConditions(
        @Path("id") id: String
    ): Response<GetLovedOneMedicalConditionsResponseModel>

    @GET(ApiConstants.LovedOne.GET_LOVED_ONE_DETAIL_WITH_RELATION)
    suspend fun getLovedOneDetailWithRelation(
        @Path("id") id: String
    ): Response<UserDetailsResponseModel>

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

    @GET(ApiConstants.Authentication.USER_RESEND_VERIFICATION)
    suspend fun sendUserVerificationEmail(): Response<BaseResponseModel>

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
        @Query("loved_one_user_id") loved_one_user_uid: String
    ): Response<AddedEventResponseModel>

    @GET(ApiConstants.Event.GET_EVENT_DETAIL)
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Response<EventDetailResponseModel>


    @POST(ApiConstants.MedicalConditions.CREATE_BULK_ONE_CONDITIONS)
    suspend fun createBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

    @PUT(ApiConstants.MedicalConditions.UPDATE_MEDICAL_CONDITIONS)
    suspend fun updateMedicalConditions(@Body value: UpdateMedicalConditionRequestModel): Response<BaseResponseModel>

    @POST(ApiConstants.MedicalConditions.EDIT_BULK_ONE_CONDITIONS)
    suspend fun editBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

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
    suspend fun searchCareTeamsByLovedOneId(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: Int,
        @Query("loved_one_id") lovedOneUUID: String,
        @Query("search") search: String,
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

    @PUT(ApiConstants.Invitations.ACCEPT_INVITATIONS)
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

    //---------LockBox---------------------------------------
    @GET(ApiConstants.LockBox.GET_ALL_LOCK_BOX_TYPES)
    suspend fun getAllLockBoxTypes(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("love_user_id") loveone_user_id: String
        //,@Query("is_query") type: Boolean
    ): Response<LockBoxTypeResponseModel>

    @Multipart
    @POST(ApiConstants.LockBox.UPLOAD_LOCK_BOX_DOC)
    suspend fun uploadLockBoxDoc(
        @Part lockBoxDoc: MultipartBody.Part?
    ): Response<UploadLockBoxDocResponseModel>

    @Multipart
    @POST(ApiConstants.LockBox.UPLOAD_MULTIPLE_LOCK_BOX_DOC)
    suspend fun uploadMultipleLockBoxDoc(
        @Part lockBoxDoc: ArrayList<MultipartBody.Part?>
    ): Response<UploadMultipleLockBoxDoxResponseModel>

    @POST(ApiConstants.LockBox.CREATE_LOCK_BOX)
    suspend fun addNewLockBox(
        @Body addNewLockBoxRequestModel: AddNewLockBoxRequestModel
    ): Response<AddNewLockBoxResponseModel>

    @GET(ApiConstants.LockBox.EDIT_LOCK_BOX)
    suspend fun getDetailLockBox(
        @Path("id") id: Int
    ): Response<AddNewLockBoxResponseModel>

    @POST(ApiConstants.LockBox.EDIT_LOCK_BOX)
    suspend fun editNewLockBox(
        @Body addNewLockBoxRequestModel: AddNewLockBoxRequestModel,
        @Path("id") id: Int
    ): Response<AddNewLockBoxResponseModel>

    @GET(ApiConstants.LockBox.GET_ALL_UPLOADED_DOCUMENTS_BY_LOVED_ONE_UUID)
    suspend fun getAllUploadedDocumentsByLovedOneUUID(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("love_user_id") lovedOneUUID: String
    ): Response<UploadedLockBoxDocumentsResponseModel>

    @GET(ApiConstants.LockBox.GET_ALL_UPLOADED_DOCUMENTS_BY_LOVED_ONE_UUID)
    suspend fun searchAllUploadedDocumentsByLovedOneUUID(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("love_user_id") lovedOneUUID: String,
        @Query("search") search: String
    ): Response<UploadedLockBoxDocumentsResponseModel>

    @DELETE(ApiConstants.LockBox.DELETE_UPLOADED_LOCK_BOX_DOC)
    suspend fun deleteUploadedLockBoxDoc(
        @Path("id") id: Int
    ): Response<DeleteUploadedLockBoxDocResponseModel>

    @PUT(ApiConstants.LockBox.UPDATE_LOCK_BOX_DOC)
    suspend fun updateLockBox(
        @Path("id") id: Int?,
        @Body updateLockBoxRequestModel: UpdateLockBoxRequestModel
    ): Response<UpdateLockBoxResponseModel>

    //---------MedList---------------------------------------

    @GET(ApiConstants.MedList.GET_ALL_MED_LIST)
    suspend fun getAllMedLists(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
//        @Query("search") search: String = ""
    ): Response<GetAllMedListResponseModel>

    @GET(ApiConstants.MedList.GET_ALL_MED_LIST)
    suspend fun searchMedList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): Response<GetAllMedListResponseModel>


    @GET(ApiConstants.MedList.GET_ALL_DOSE_LIST)
    suspend fun getAllDose(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<GetAllDoseListResponseModel>

    @GET(ApiConstants.MedList.GET_ALL_DOSE_TYPE_LIST)
    suspend fun getAllDoseType(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<GetAllDoseListResponseModel>

    @GET(ApiConstants.MedList.GET_LOVED_ONE_MED_LIST)
    suspend fun getLovedOneMedList(
        @Path("id") id: String, @Query("date") date: String
    ): Response<GetLovedOneMedList>

    // scheduled medication
    @POST(ApiConstants.MedList.ADD_SCHEDULED_MEDICATION)
    suspend fun addScheduledMedication(
        @Body addNewLockBoxRequestModel: ScheduledMedicationRequestModel
    ): Response<AddScheduledMedicationResponseModel>

    @PUT(ApiConstants.MedList.UPDATE_SCHEDULED_MEDICATION)
    suspend fun updateScheduledMedication(
        @Path("id") id: Int,
        @Body addNewLockBoxRequestModel: UpdateScheduledMedList
    ): Response<AddScheduledMedicationResponseModel>

    @DELETE(ApiConstants.MedList.DELETE_SCHEDULED_MEDICATION)
    suspend fun deleteAddedMedication(
        @Path("id") id: Int
    ): Response<DeleteAddedMedicationResponseModel>

    @POST(ApiConstants.MedList.ADD_USER_MEDICATION_RECORD)
    suspend fun addUserMedicationRecord(
        @Body medicationRecordRequestModel: MedicationRecordRequestModel
    ): Response<MedicationRecordResponseModel>

    @GET(ApiConstants.MedList.GET_MEDICATION_DETAIL)
    suspend fun getMedicationDetails(
        @Path("id") id: Int
    ): Response<GetMedicationDetailResponse>

    @GET(ApiConstants.MedList.GET_MEDICATION_RECORD)
    suspend fun getMedicationRecords(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("date") date: String
    ): Response<GetMedicationRecordResponse>

    @PUT(ApiConstants.UpdateProfile.UPDATE_LOGIN_USER_PROFILE)
    suspend fun updateProfile(
        @Body value: UserUpdateData,
        @Path("id") id: Int
    ): Response<EditResponseModel>

    // add vital stats for loginLoved one
    @POST(ApiConstants.VitalStats.ADD_VITAL_STATS)
    suspend fun addVitalStats(
        @Body addNewLockBoxRequestModel: VitalStatsRequestModel
    ): Response<AddVitalStatsResponseModel>

    // get vital stats for loginLoved one
    @GET(ApiConstants.VitalStats.ADD_VITAL_STATS)
    suspend fun getVitalStats(
        @Query("date") date: String,
        @Query("loveone_user_id") loveone_user_id: String,
        @Query("type") type: String
    ): Response<VitalStatsResponseModel>

    // get vital stats for loginLoved one
    @GET(ApiConstants.VitalStats.GET_GRAPH_VITAL_STATS)
    suspend fun getGraphDataVitalStats(
        @Query("date") date: String,
        @Query("loveone_user_id") loveone_user_id: String,
        @Query("type") type: String
    ): Response<VitalStatsResponseModel>

    // send security code
    @POST(ApiConstants.SecurityCode.ADD_SECURITY_CODE)
    suspend fun addSecurityCode(
        @Body sendSecurityCodeRequestModel: SendSecurityCodeRequestModel
    ): Response<BaseResponseModel>

    // resend security code
    @POST(ApiConstants.SecurityCode.CHANGE_SECURITY_CODE)
    suspend fun resetSecurityCode(
        @Body sendSecurityCodeRequestModel: SendSecurityCodeRequestModel
    ): Response<SecurityCodeResponseModel>


    @GET(ApiConstants.Resource.GET_ALL_RESOURCE)
    suspend fun getAllResourceApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("id") id: String,
        @Query("conditions") conditions: String
    ): Response<ResponseRelationModel>

    @GET(ApiConstants.Resource.GET_ALL_RESOURCE)
    suspend fun getSearchResourceResultApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("id") id: String,
        @Query("search") search: String
    ): Response<ResponseRelationModel>
// @Query("conditions") conditions:String,

    @GET(ApiConstants.Resource.GET_RESOURCE_DETAIL)
    suspend fun getResourceDetail(
        @Path("id") id: Int,
    ): Response<ParticularResourceResponseModel>


    @GET(ApiConstants.Resource.GET_TRENDING_RESOURCE)
    suspend fun getTrendingResourceApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<ResponseRelationModel>

    @GET(ApiConstants.Settings.GET_STATIC_PAGE)
    suspend fun getStaticPagesApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<StaticPageResponseModel>
}