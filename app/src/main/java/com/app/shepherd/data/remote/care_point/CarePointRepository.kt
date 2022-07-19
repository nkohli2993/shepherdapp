package com.app.shepherd.data.remote.care_point
import com.app.shepherd.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.app.shepherd.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.app.shepherd.data.dto.added_events.AddedEventResponseModel
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberResponseModel
import com.app.shepherd.data.dto.invitation.InvitationsResponseModel
import com.app.shepherd.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarePointRepository @Inject constructor(private val apiService: ApiService) {

    // Get Care points for loggedIn User
    suspend fun getCarePointsAdded(
        pageNumber: Int,
        limit: Int,
        start_date:String,
        end_date:String
    ): Flow<DataResult<AddedEventResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddedEventResponseModel, AddedEventResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddedEventResponseModel> {
                return apiService.getCreatedEvent(pageNumber, limit,start_date,end_date)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}