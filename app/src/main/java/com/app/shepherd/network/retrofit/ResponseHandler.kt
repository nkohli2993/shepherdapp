
import android.util.MalformedJsonException
import com.app.shepherd.network.retrofit.Resource
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

open class ResponseHandler {
    fun <T : Any> handleResponse(data: T): Resource<T> {
        return Resource.success(data)
    }

    // TODO HANDLE ALL ERROR CASES

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e), null)
            is MalformedJsonException -> Resource.error(getErrorMessage(46456), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(25345), null)
            is IOException -> Resource.error(getErrorMessage(403), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(e: HttpException): String {
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

    fun getErrorMsg(responseBody: ResponseBody): String {

        try {
            val jsonObject = JSONObject(responseBody.string())

            return jsonObject.getString("message")

        } catch (e: Exception) {
            return e.message!!
        }

    }

}