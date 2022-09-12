import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.LocationManager
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.inSpans
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*


object CommonFunctions {
    var mDialogProgress: AlertDialog? = null


    fun getDeviceId(context: Context): String? {
        var deviceId: String? = ""
        try {
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deviceId
    }


    fun hideKeyBoard(activity: Context, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun showKeyBoard(activity: Context) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun setColorForPath(spannable: Spannable, paths: Array<String>, color: Int) {
        for (i in paths.indices) {
            val indexOfPath = spannable.toString().indexOf(paths[i])
            if (indexOfPath == -1) {
                continue
            }
            spannable.setSpan(
                ForegroundColorSpan(color), indexOfPath,
                indexOfPath + paths[i].length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    fun fullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (activity.window != null && activity.window.insetsController != null) {
                val insetsController = activity.window.insetsController
                if (insetsController != null) {
                    insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    insetsController.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    @SuppressLint("Range")
    fun getLastCaptureImage(activity: Activity): String {
        try {
            val imageColumns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
            val imageOrderBy = MediaStore.Images.Media._ID + " DESC"
            val imageCursor: Cursor = activity.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns,
                null,
                null,
                imageOrderBy
            )
            imageCursor.moveToFirst()
            do {
                val fullPath: String =
                    imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                if (fullPath.contains("DCIM")) {
                    Log.d("last_captures", fullPath)
                    return fullPath
                }
            } while (imageCursor.moveToNext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getString(string: String?): String {
        if (string == null)
            return ""

        return string.trim()
    }

    fun String.toMultiPartRequestBody(): RequestBody {
        return this.toRequestBody("text/plain".toMediaType())
    }

    fun isImage(url: String): Boolean {
        val filenameArray = url.split(".")
        val extension = filenameArray[filenameArray.size - 1]
        return (extension.endsWith("png") || extension.endsWith("jpeg")
                || extension.endsWith("jpg"))
    }

    fun isDocument(url: String): Boolean {
        val filenameArray = url.split(".")
        val extension = filenameArray[filenameArray.size - 1]
        return (extension.endsWith("doc") || extension.endsWith("docx")
                || extension.endsWith("pdf") || extension.endsWith("xls"))
    }


    /* fun loadCircularImage(context:Context,imageUrl:String,circularImageView:CircleImageView){
         var requestOptions = RequestOptions()
         requestOptions.placeholder( R.drawable.ic_user_placeholder)
         Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(circularImageView)
     }*/

    fun openDocument(context: Context, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    }

    fun getListOfHrs(): ArrayList<String> {
        val listHrs: ArrayList<String> = ArrayList()
        listHrs.add("Hours")
        for (i in 0..10) {
            if (i == 0 || i == 1)
                listHrs.add("0$i hrs")
            //listHrs.add("0$i hrs")
            else if (i.toString().length == 1)
                listHrs.add("0$i hrs")
            else
                listHrs.add("$i hrs")
        }
        return listHrs
    }

    fun getListOfMinutes(): ArrayList<String> {
        val listMins: ArrayList<String> = ArrayList()
        listMins.add("Minutes")
        for (i in 0..59) {
            if (i == 0 || i == 1)
                listMins.add("0$i mins")
            //listMins.add("0$i min")
            else if (i.toString().length == 1)
                listMins.add("0$i mins")
            else
                listMins.add("$i mins")
        }
        return listMins
    }


/*
    fun onShareClick(activity: Context) {
        val shareBody: String =
            "Join me on JSW, an application that make your business handling easier.\n" + getInvitationLink()
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        activity.startActivity(
            Intent.createChooser(
                sharingIntent,
                activity.resources.getString(R.string.share_using)
            )
        )


    }
*/

    private fun getInvitationLink(): String? {
        val playStoreLink =
            "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&referrer=utm_source="
        return playStoreLink;
    }


    fun getMediaDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(METADATA_KEY_DURATION)
        retriever.release()
        return duration?.toLongOrNull() ?: 0
    }


    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName =  contentUri.toString().substring(contentUri.toString().lastIndexOf("/") + 1).replace("%3","_") + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val name = if(fileName.length>30){
            "temp_file_${Calendar.getInstance().timeInMillis}$fileExtension".plus(".$fileExtension")
        }
        else{
            fileName
        }
        val tempFile = File(context.cacheDir, name)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }


    private fun isOverlapping(start1: Date, end1: Date, start2: Date?, end2: Date?): Boolean {
        return start1 <= end2 && end1 >= start2
    }

    fun locationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun resizeBitmap(context: Context, drawableName: String?, width: Int, height: Int): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(
            context.getResources(),
            context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName())
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    fun replaceCommaWithAnd(serviceList: String): String? {
        return if (serviceList.contains(",")) {
            val builder = StringBuilder(serviceList)
            val lastIndex: Int = serviceList.lastIndexOf(",")
            builder.replace(lastIndex, lastIndex + 1, " and")
            builder.toString()
        } else
            serviceList
    }

    fun getMediumFont(context: Context): Typeface {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.resources.getFont(R.font.poppins_medium)
        else context.let { ResourcesCompat.getFont(it, R.font.poppins_medium) }!!
    }

    inline fun SpannableStringBuilder.font(
        typeface: Typeface? = null,
        builderAction: SpannableStringBuilder.() -> Unit
    ) =
        inSpans(StyleSpan(typeface?.style ?: Typeface.DEFAULT.style), builderAction = builderAction)


    fun showDefaultDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonText) { _, _ ->

            }
            setNegativeButton(negativeButtonText) { _, _ ->
            }
            setNeutralButton("Neutral") { _, _ ->
            }
        }.create().show()
    }

}