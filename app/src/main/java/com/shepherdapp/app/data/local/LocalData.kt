package com.shepherdapp.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.shepherdapp.app.FAVOURITES_KEY
import com.shepherdapp.app.SHARED_PREFERENCES_FILE_NAME
import com.shepherdapp.app.data.Resource
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */

class LocalData @Inject constructor(val context: Context) {

    /*fun doLogin(loginRequest: LoginRequestModel): Resource<LoginResponseModel> {
        if (loginRequest == LoginRequestModel("sumit@sumit.sumit", "sumit")) {
            return Resource.Success(
                LoginResponseModel("123", "Ahmed", "Mahmoud",
                    "Phase 6 Mohali", "77", "160055", "Chandigarh",
                    "India", "sumit@sumit.sumit")
            )
        }
        return Resource.DataError(PASS_WORD_ERROR)
    }*/

    fun getCachedFavourites(): Resource<Set<String>> {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
        return Resource.Success(sharedPref.getStringSet(FAVOURITES_KEY, setOf()) ?: setOf())
    }

    fun isFavourite(id: String): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
        val cache = sharedPref.getStringSet(FAVOURITES_KEY, setOf<String>()) ?: setOf()
        return Resource.Success(cache.contains(id))
    }

    fun cacheFavourites(ids: Set<String>): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putStringSet(FAVOURITES_KEY, ids)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }

    fun removeFromFavourites(id: String): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
        var set = sharedPref.getStringSet(FAVOURITES_KEY, mutableSetOf<String>())?.toMutableSet()
            ?: mutableSetOf()
        if (set.contains(id)) {
            set.remove(id)
        }
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        editor.commit()
        editor.putStringSet(FAVOURITES_KEY, set)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }
}

