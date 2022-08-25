package com.shepherd.app.network.retrofit
import android.util.Log
import androidx.annotation.MainThread
import com.shepherd.app.ui.base.BaseResponseModel
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

/**
 * Created by Deepak Rattan on 27/05/22
 */
abstract class NetworkOnlineDataRepo<RESULT, REQUEST> {
    fun asFlow() = flow {
        emit(DataResult.Loading())
        try {
            val apiResponse = fetchDataFromRemoteSource()
            val data = apiResponse.body()

            if (apiResponse.isSuccessful && data != null) {

                if (validateData(data))
                    emit(DataResult.Success(data))
                else {
                    getErrorResponse(data)?.let {
                        emit(
                            DataResult.Failure(
                                message = it.message,errorCode = apiResponse.code(), error=  (it.error.let { "" })
                                /*it.apiVersion?.toInt()*/
                            )
                        )
                    } ?: emit(
                        DataResult.Failure(
                            "Some thing went wrong, try again later!",
                            7887877
                        )
                    )
                }


            } else {
                // emit(com.shepherd.app.network.retrofit.DataResult.Failure("Something went wrong!"))
                /*if (apiResponse.code() == 401) {
                    // logout the user
                    emit(com.shepherd.app.network.retrofit.DataResult.Failure(getErrorMsg(apiResponse.errorBody()!!),false,apiResponse.code()))

                 } else {
                    emit(com.shepherd.app.network.retrofit.DataResult.Failure(getErrorMsg(apiResponse.errorBody()!!),true,apiResponse.code()))
                }*/

                emit(DataResult.Failure(getErrorMsg(apiResponse.errorBody()!!), apiResponse.code()))

            }
        } catch (e: Exception) {
            emit(
                DataResult.Failure(
                    e.message, null
                )
            )

            Log.e("NetworkCallException", e.message.toString())
        }

    }

    private fun validateData(data: REQUEST): Boolean {
        if (data is BaseResponseModel) {
//            if (data.statusCode == 200) {
                return true
//            }
        }
        return false
    }

    private fun getErrorResponse(data: REQUEST): BaseResponseModel? {
        return if (data is BaseResponseModel) {
            data
        } else {
            null
        }
    }

    //    private fun getErrorMessage(e: HttpException): String {
//        return getErrorMsg(e.response()?.errorBody()!!)
//    }
    fun getErrorMsg(responseBody: ResponseBody): String {

        return try {
            val jsonObject = JSONObject(responseBody.string())

            jsonObject.getString("msg")

        } catch (e: Exception) {
            e.message!!
        }

    }


    /* fun   handleException(e: Exception): Resource<T> {
         return when (e) {
             is HttpException -> Resource.error(getErrorMessage(e), null)
             is MalformedJsonException -> Resource.error(getErrorMessage(46456), null)
             is SocketTimeoutException -> Resource.error(getErrorMessage(25345), null)
             is IOException -> Resource.error(getErrorMessage(403), null)
             else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
         }
     }*/

    /* private fun getErrorMessage(e: HttpException): String {
         return getErrorMsg(e.response()?.errorBody()!!)
     }

     private fun getErrorMessage(code: Int): String {
         return when (code) {
             //   ErrorCodes.SocketTimeOut.code -> "Timeout"
             401 -> "Unauthorised"
             404 -> "Not found"
             403 -> "Internet Connection Not Found"
             else -> "Something went wrong"
         }
     }
  */


    @MainThread
    protected abstract suspend fun fetchDataFromRemoteSource(): Response<REQUEST>
}