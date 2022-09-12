package com.shepherdapp.app.data.remote.care_point

import com.shepherdapp.app.data.dto.added_events.*
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
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
        start_date: String,
        end_date: String,
        loved_one_user_uid: String
    ): Flow<DataResult<AddedEventResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddedEventResponseModel, AddedEventResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddedEventResponseModel> {
                return apiService.getCreatedEvent(
                    pageNumber,
                    limit,
                    start_date,
                    end_date,
                    loved_one_user_uid
                )
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

    // Get Care points comments for event
    suspend fun getEventCommentsIdBased(
        page: Int, limit: Int, id: Int
    ): Flow<DataResult<AllCommentEventsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AllCommentEventsResponseModel, AllCommentEventsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AllCommentEventsResponseModel> {
                return apiService.getEventComment(page, limit, id)
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