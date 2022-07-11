package com.app.shepherd.data

import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.login.LoginRequestModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.recipes.Recipes
import com.app.shepherd.data.local.LocalData
import com.app.shepherd.data.remote.RemoteData
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.NetworkOnlineDataRepo
import com.app.shepherd.ui.component.addNewEvent.CreateEventModel
import com.app.shepherd.ui.component.addNewEvent.CreateEventResponseModel
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by Sumit Kumar
 */

class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext,
    private val apiService: ApiService
) : DataRepositorySource {

    override suspend fun requestRecipes(): Flow<Resource<Recipes>> {
        return flow {
            emit(remoteRepository.requestRecipes())
        }.flowOn(ioDispatcher)
    }

    override suspend fun doLogin(loginRequest: LoginRequestModel): Flow<Resource<LoginResponseModel>> {
        TODO("Not yet implemented")
    }

    /* override suspend fun doLogin(loginRequest: LoginRequestModel): Flow<Resource<LoginResponseModel>> {
         return flow {
             emit(localRepository.doLogin(loginRequest))
         }.flowOn(ioDispatcher)
     }*/

    override suspend fun addToFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            localRepository.getCachedFavourites().let {
                it.data?.toMutableSet()?.let { set ->
                    val isAdded = set.add(id)
                    if (isAdded) {
                        emit(localRepository.cacheFavourites(set))
                    } else {
                        emit(Resource.Success(false))
                    }
                }
                it.errorCode?.let { errorCode ->
                    emit(Resource.DataError<Boolean>(errorCode))
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.removeFromFavourites(id))
        }.flowOn(ioDispatcher)
    }

    override suspend fun isFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.isFavourite(id))
        }.flowOn(ioDispatcher)
    }

    suspend fun getMembers(
        pageNumber: Int,
        limit: Int,
        status: Int,
        loved_one_id: String?
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getMembers(pageNumber, limit, status, loved_one_id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    suspend fun createEvent(createEventModel: CreateEventModel): Flow<DataResult<CreateEventResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CreateEventResponseModel, CreateEventResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CreateEventResponseModel> {
                return apiService.createEvent(createEventModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

}
