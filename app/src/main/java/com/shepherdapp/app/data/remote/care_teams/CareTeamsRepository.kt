package com.shepherdapp.app.data.remote.care_teams

import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberResponseModel
import com.shepherdapp.app.data.dto.invitation.InvitationsResponseModel
import com.shepherdapp.app.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Deepak Rattan on 08/06/22
 */
@Singleton
class CareTeamsRepository @Inject constructor(private val apiService: ApiService) {

    // Get Care Teams for loggedIn User
    suspend fun getCareTeamsForLoggedInUser(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getCareTeamsForLoggedInUser(pageNumber, limit, status)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get Care Teams for loggedIn User
    suspend fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int,
        lovedOneUUID: String
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getCareTeamsByLovedOneId(pageNumber, limit, status, lovedOneUUID)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Search Care Teams for loggedIn User
    suspend fun searchCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int,
        lovedOneUUID: String,
        search: String
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.searchCareTeamsByLovedOneId(
                    pageNumber,
                    limit,
                    status,
                    lovedOneUUID,
                    search
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    // Get Care Teams
    suspend fun getCareTeamRoles(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getCareTeamRoles(pageNumber, limit /*status*/)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Add New Care Team Member
    suspend fun addNewCareTeamMember(newCareTeamMember: AddNewMemberCareTeamRequestModel): Flow<DataResult<AddNewMemberCareTeamResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddNewMemberCareTeamResponseModel, AddNewMemberCareTeamResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddNewMemberCareTeamResponseModel> {
                return apiService.addNewMemberCareTeam(newCareTeamMember)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Delete Care Team Member
    suspend fun deleteCareTeamMember(id: Int): Flow<DataResult<DeleteCareTeamMemberResponseModel>> {
        return object :
            NetworkOnlineDataRepo<DeleteCareTeamMemberResponseModel, DeleteCareTeamMemberResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<DeleteCareTeamMemberResponseModel> {
                return apiService.deleteCareTeamMember(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Update Care Team Member
    suspend fun updateCareTeamMember(
        id: Int,
        updateCareTeamMemberRequestModel: UpdateCareTeamMemberRequestModel
    ): Flow<DataResult<UpdateCareTeamMemberResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UpdateCareTeamMemberResponseModel, UpdateCareTeamMemberResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UpdateCareTeamMemberResponseModel> {
                return apiService.updateCareTeamMember(id, updateCareTeamMemberRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get Join Care Team Invitations
    suspend fun getJoinCareTeamInvitations(
        sendType: String,
        status: Int
    ): Flow<DataResult<InvitationsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<InvitationsResponseModel, InvitationsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<InvitationsResponseModel> {
                return apiService.getInvitations(sendType, status)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Accept Care Team Invitation
    suspend fun acceptInvitation(id: Int): Flow<DataResult<AcceptInvitationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AcceptInvitationResponseModel, AcceptInvitationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AcceptInvitationResponseModel> {
                return apiService.acceptInvitation(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}