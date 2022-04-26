package com.app.shepherd.data

import com.app.shepherd.data.dto.recipes.Recipes
import com.app.shepherd.data.dto.login.LoginRequest
import com.app.shepherd.data.dto.login.LoginResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sumit Kumar
 */

interface DataRepositorySource {
    suspend fun requestRecipes(): Flow<Resource<Recipes>>
    suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun addToFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun isFavourite(id: String): Flow<Resource<Boolean>>
}
