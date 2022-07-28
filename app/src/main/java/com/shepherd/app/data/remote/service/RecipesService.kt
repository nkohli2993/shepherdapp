package com.shepherd.app.data.remote.service

import com.shepherd.app.data.dto.recipes.RecipesItem
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by Sumit Kumar
 */

interface RecipesService {
    @GET("recipes.json")
    suspend fun fetchRecipes(): Response<List<RecipesItem>>
}
