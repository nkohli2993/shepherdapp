package com.shepherdapp.app.network.retrofit

import com.shepherdapp.app.constants.ApiConstants
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherdapp.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.VitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals.BulkCreateVitalRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.update_user_profile_last_sync.UpdateUserProfileForLastSyncRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.update_user_profile_last_sync.UpdateUserProfileForLastSyncResponseModel
import com.shepherdapp.app.data.dto.added_events.*
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberResponseModel
import com.shepherdapp.app.data.dto.change_password.ChangePasswordModel
import com.shepherdapp.app.data.dto.chat.ChatNotificationModel
import com.shepherdapp.app.data.dto.dashboard.HomeResponseModel
import com.shepherdapp.app.data.dto.delete_account.DeleteAccountModel
import com.shepherdapp.app.data.dto.edit_event.EditEventRequestModel
import com.shepherdapp.app.data.dto.edit_event.EditEventResponseModel
import com.shepherdapp.app.data.dto.edit_loved_one.EditLovedOneResponseModel
import com.shepherdapp.app.data.dto.edit_profile.UserUpdateData
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseRequestModel
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseResponseModel
import com.shepherdapp.app.data.dto.forgot_password.ForgotPasswordModel
import com.shepherdapp.app.data.dto.invitation.InvitationsResponseModel
import com.shepherdapp.app.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
import com.shepherdapp.app.data.dto.invitation.delete_pending_invitee.DeletePendingInviteeByIdResponseModel
import com.shepherdapp.app.data.dto.invitation.pending_invite.PendingInviteResponseModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherdapp.app.data.dto.lock_box.edit_lock_box.EditLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.shepherdapp.app.data.dto.lock_box.share_lock_box.ShareLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.shepherdapp.app.data.dto.lock_box.upload_multiple_lock_box_doc.UploadMultipleLockBoxDoxResponseModel
import com.shepherdapp.app.data.dto.login.EditResponseModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.med_list.*
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddMedListRequestModel
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddedMedlistResponseModel
import com.shepherdapp.app.data.dto.med_list.get_medication_detail.GetMedicationDetailResponse
import com.shepherdapp.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherdapp.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherdapp.app.data.dto.med_list.medication_record.MedicationRecordResponseModel
import com.shepherdapp.app.data.dto.medical_conditions.*
import com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions.EditMedicalConditionsResponseModel
import com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherdapp.app.data.dto.notification.NotificationResponseModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationRequestModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationsResponseModel
import com.shepherdapp.app.data.dto.push_notification.FCMResponseModel
import com.shepherdapp.app.data.dto.relation.RelationResponseModel
import com.shepherdapp.app.data.dto.resource.GetCategoriesResponseModel
import com.shepherdapp.app.data.dto.resource.ParticularResourceResponseModel
import com.shepherdapp.app.data.dto.resource.ResponseRelationModel
import com.shepherdapp.app.data.dto.roles.RolesResponseModel
import com.shepherdapp.app.data.dto.security_code.SecurityCodeResponseModel
import com.shepherdapp.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherdapp.app.data.dto.settings_pages.StaticPageResponseModel
import com.shepherdapp.app.data.dto.signup.BioMetricData
import com.shepherdapp.app.data.dto.signup.UserSignupData
import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.SubscriptionResponseModel
import com.shepherdapp.app.data.dto.subscription.check_subscription_status.CheckSubscriptionStatusResponseModel
import com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions.GetPreviousSubscriptionsResponseModel
import com.shepherdapp.app.data.dto.subscription.get_active_subscriptions.GetActiveSubscriptionResponseModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionResponseModel
import com.shepherdapp.app.data.dto.user.UserDetailsResponseModel
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventModel
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventResponseModel
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

    @POST(ApiConstants.Authentication.DELETE_ACCOUNT)
    suspend fun deleteAccount(
        @Path("id") uuid: Int,
        @Body value: DeleteAccountModel
    ): Response<BaseResponseModel>

    @POST(ApiConstants.Authentication.DELETE_ACCOUNT)
    suspend fun deleteAccountWithoutReason(
        @Path("id") uuid: Int,
    ): Response<BaseResponseModel>

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
        @Path("uuid") uuid: String?,
        @Body value: CreateLovedOneModel
    ): Response<EditLovedOneResponseModel>

    @GET(ApiConstants.MedicalConditions.GET_MEDICAL_CONDITIONS)
    suspend fun getMedicalConditions(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
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

    @PUT(ApiConstants.Event.EDIT_EVENT)
    suspend fun editEvent(
        @Body editEventRequestModel: EditEventRequestModel,
        @Path("id") id: Int
    ): Response<EditEventResponseModel>


    @POST(ApiConstants.MedicalConditions.CREATE_BULK_ONE_CONDITIONS)
    suspend fun createBulkOneConditions(@Body value: ArrayList<MedicalConditionsLovedOneRequestModel>): Response<UserConditionsResponseModel>

    @PUT(ApiConstants.MedicalConditions.UPDATE_MEDICAL_CONDITIONS)
    suspend fun updateMedicalConditions(@Body value: UpdateMedicalConditionRequestModel): Response<BaseResponseModel>

    @POST(ApiConstants.MedicalConditions.ADD_MEDICAL_CONDITION)
    suspend fun addMedicalConditions(@Body value: AddMedicalConditionRequestModel): Response<AddedUserMedicalConditionResposneModel>

    @PUT(ApiConstants.MedicalConditions.EDIT_MEDICAL_CONDITION)
    suspend fun editMedicalConditions(
        @Body value: AddMedicalConditionRequestModel,
        @Path("id") id: Int
    ): Response<EditMedicalConditionsResponseModel>

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

    @GET(ApiConstants.Invitations.GET_PENDING_INVITATIONS)
    suspend fun getPendingInvites(
        @Path("id") lovedOneUUID: String?
    ): Response<PendingInviteResponseModel>

    @DELETE(ApiConstants.Invitations.DELETE_PENDING_INVITEE_BY_ID)
    suspend fun deletePendingInviteeById(
        @Path("id") id: Int
    ): Response<DeletePendingInviteeByIdResponseModel>

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

    @PUT(ApiConstants.LockBox.EDIT_LOCK_BOX)
    suspend fun editNewLockBox(
        @Body addNewLockBoxRequestModel: EditLockBoxRequestModel,
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

    @PUT(ApiConstants.LockBox.SHARE_LOCK_BOX)
    suspend fun shareLockBoxDoc(
        @Path("id") id: Int?
    ): Response<ShareLockBoxResponseModel>

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
        @Path("id") id: String/*, @Query("date") date: String*/
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

    @POST(ApiConstants.MedList.ADD_MED_LIST)
    suspend fun addNewMedlistMedicine(@Body medlist: AddMedListRequestModel): Response<AddedMedlistResponseModel>

    @PUT(ApiConstants.MedList.UPDATE_MED_LIST)
    suspend fun editMedList(
        @Body medList: AddMedListRequestModel,
        @Path("id") id: Int
    ): Response<AddedMedlistResponseModel>

    @PUT(ApiConstants.UpdateProfile.UPDATE_LOGIN_USER_PROFILE)
    suspend fun updateProfile(
        @Body value: UserUpdateData,
        @Path("id") id: Int
    ): Response<EditResponseModel>

    @PUT(ApiConstants.UpdateProfile.UPDATE_USER_PROFILE_FOR_LAST_SYNC)
    suspend fun updateProfileForLastSync(
        @Body updateUserProfileForLastSyncRequestModel: UpdateUserProfileForLastSyncRequestModel,
        @Path("id") id: Int
    ): Response<UpdateUserProfileForLastSyncResponseModel>

    // add vital stats for loginLoved one
    @POST(ApiConstants.VitalStats.ADD_VITAL_STATS)
    suspend fun addVitalStats(
        @Body addNewLockBoxRequestModel: VitalStatsRequestModel
    ): Response<AddVitalStatsResponseModel>

    // Bulk Create Vital for loginLoved one
    @POST(ApiConstants.VitalStats.BULK_CREATE_VITAL)
    suspend fun createBulkVitalStats(
        @Body bulkCreateVitalRequestModel: BulkCreateVitalRequestModel
    ): Response<BaseResponseModel>

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


    @GET(ApiConstants.Resource.GET_ALL_RESOURCE_BY_LOVED_ONE)
    suspend fun getAllResourceApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("id") id: String,
        @Query("conditions") conditions: String?
    ): Response<ResponseRelationModel>

    @GET(ApiConstants.Resource.GET_ALL_RESOURCE)
    suspend fun getAllResources(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<ResponseRelationModel>


    @GET(ApiConstants.Resource.GET_ALL_RESOURCE)
    suspend fun getAllResourcesAsPerCategory(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("categoryIds") categoryIds: String,
    ): Response<ResponseRelationModel>

    @GET(ApiConstants.Resource.GET_ALL_RESOURCE)
    suspend fun getSearchResourceResultApi(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): Response<ResponseRelationModel>

    @GET(ApiConstants.Resource.GET_CATEGORIES)
    suspend fun getResourceCategories(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<GetCategoriesResponseModel>

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

    @GET(ApiConstants.Notification.GET_NOTIFICATION_LIST)
    suspend fun getNotificationListBasedOnLovedOne(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("loveone_user_id") loveone_user_id: String
    ): Response<NotificationResponseModel>

    @GET(ApiConstants.Notification.GET_USER_NOTIFICATIONS)
    suspend fun getUserNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("loved_one_id") love_user_id: String
    ): Response<NotificationResponseModel>

    @GET(ApiConstants.Notification.GET_NOTIFICATIONS)
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("loved_one_id") lovedOneUUID: String,
    ): Response<NotificationResponseModel>

    @PUT(ApiConstants.Notification.READ_NOTIFICATIONS)
    suspend fun readNotifications(
        @Body readNotificationRequestModel: ReadNotificationRequestModel?
    ): Response<ReadNotificationsResponseModel>

    @PUT(ApiConstants.Notification.READ_NOTIFICATIONS)
    suspend fun clearNotifications(): Response<ReadNotificationsResponseModel>

    @Headers(
        "Authorization: key=AAAAOIHQQEc:APA91bHfsqzVnLwnQZt9qhU9nJVOq3utYheRYYHQl1IrBFTfb_yM5js6gPu8eNzMrYcZjAbeAV_nxm73CZKBnJEwYPZ30YYZkOrLVI82l9AtlV_4FRg0hj0p0h_GgUClE6dgpXWsVJgg",
        "Content-Type:application/json"
    )
    @POST(ApiConstants.Notification.SEND_PUSH_NOTIFICATIONS)
    suspend fun sendPushNotification(
//        @Header ("Authorization") "AAAAOIHQQEc:APA91bHfsqzVnLwnQZt9qhU9nJVOq3utYheRYYHQl1IrBFTfb_yM5js6gPu8eNzMrYcZjAbeAV_nxm73CZKBnJEwYPZ30YYZkOrLVI82l9AtlV_4FRg0hj0p0h_GgUClE6dgpXWsVJgg"
        @Body chatNotificationModel: ChatNotificationModel
    ): Response<FCMResponseModel>

    @POST(ApiConstants.Subscription.VALIDATE_SUBSCRIPTION)
    suspend fun validateSubscription(@Body validateSubscriptionRequestModel: ValidateSubscriptionRequestModel): Response<ValidateSubscriptionResponseModel>

    @POST(ApiConstants.Subscription.CREATE_SUBSCRIPTION)
    suspend fun createSubscription(@Body subscriptionRequestModel: SubscriptionRequestModel): Response<SubscriptionResponseModel>

    @GET(ApiConstants.Subscription.GET_ACTIVE_SUBSCRIPTIONS)
    suspend fun getActiveSubscriptions(): Response<GetActiveSubscriptionResponseModel>

    @GET(ApiConstants.Subscription.GET_PREVIOUS_SUBSCRIPTIONS)
    suspend fun getPreviousSubscriptions(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<GetPreviousSubscriptionsResponseModel>

    @GET(ApiConstants.Subscription.CHECK_SUBSCRIPTION_STATUS)
    suspend fun checkSubscriptionStatus(): Response<CheckSubscriptionStatusResponseModel>

    @PUT(ApiConstants.Enterprise.ATTACH_ENTERPRISE)
    suspend fun attachEnterprise(@Body attachEnterpriseRequestModel: AttachEnterpriseRequestModel): Response<AttachEnterpriseResponseModel>
}