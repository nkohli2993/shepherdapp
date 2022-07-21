package com.app.shepherd.data.remote.care_point
import com.app.shepherd.data.dto.added_events.AddedEventResponseModel
import com.app.shepherd.data.dto.added_events.EventCommentModel
import com.app.shepherd.data.dto.added_events.EventCommentResponseModel
import com.app.shepherd.data.dto.added_events.EventDetailResponseModel
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

    // Get Care points list for loggedIn User
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
  // Get Care points detail for loggedIn User
    suspend fun getCarePointsDetailIdBased(
        id: Int
    ): Flow<DataResult<EventDetailResponseModel>> {
        return object :
            NetworkOnlineDataRepo<EventDetailResponseModel, EventDetailResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<EventDetailResponseModel> {
                return apiService.getEventDetail(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Update Care Team Member
    suspend fun addEventComment(
        eventCommentModel: EventCommentModel
    ): Flow<DataResult<EventCommentResponseModel>> {
        return object :
            NetworkOnlineDataRepo<EventCommentResponseModel, EventCommentResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<EventCommentResponseModel> {
                return apiService.createEventComment(eventCommentModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


}