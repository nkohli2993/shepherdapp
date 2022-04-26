package com.app.shepherd.data.remote

import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.recipes.Recipes

/**
 * Created by Sumit Kumar
 */

internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
}
