package com.shepherdapp.app.data.remote

import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.recipes.Recipes

/**
 * Created by Sumit Kumar
 */

internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
}
