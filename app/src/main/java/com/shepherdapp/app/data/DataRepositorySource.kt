package com.shepherdapp.app.data

import com.shepherdapp.app.data.dto.recipes.Recipes
import com.shepherdapp.app.data.dto.login.LoginRequestModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sumit Kumar
 */

interface DataRepositorySource {
//    suspend fun requestRecipes(): Flow<Resource<Recipes>>
    suspend fun doLogin(loginRequest: LoginRequestModel): Flow<Resource<LoginResponseModel>>
    suspend fun addToFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun isFavourite(id: String): Flow<Resource<Boolean>>
}
