package com.shepherd.app.data.remote

import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.recipes.Recipes

/**
 * Created by Sumit Kumar
 */

internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
}
