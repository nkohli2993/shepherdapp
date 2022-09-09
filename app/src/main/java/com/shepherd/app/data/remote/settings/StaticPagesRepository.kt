package com.shepherd.app.data.remote.settings

import com.shepherd.app.data.dto.settings_pages.StaticPageResponseModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class StaticPagesRepository @Inject constructor(private val apiService: ApiService) {

    //get about terms and privacy data
    suspend fun getStaticPagesApi(page:Int,limit:Int): Flow<DataResult<StaticPageResponseModel>> {
        return object : NetworkOnlineDataRepo<StaticPageResponseModel, StaticPageResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<StaticPageResponseModel> {
                return apiService.getStaticPagesApi(page,limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}