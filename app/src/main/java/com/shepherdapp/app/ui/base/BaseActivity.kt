package com.shepherdapp.app.ui.base

import CommonFunctions
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.ui.component.vital_stats.FitActionRequestCode
import com.shepherdapp.app.ui.component.vital_stats.VitalStatsFragment
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.ProgressBarDialog
import com.shepherdapp.app.utils.extensions.checkString
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.HomeViewModel
import java.io.File
import java.util.regex.Pattern

/**
 * Created by Sumit Kumar
 */


abstract class BaseActivity : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()
    var selectedFile: MutableLiveData<File> = MutableLiveData()
    private val PERMISSION_REQUEST_CODE = 200
    private val PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS = 700
    private val PERMISSION_REQUEST_CODE_GOOGLE_FIT = 500
    private val viewModel: HomeViewModel by viewModels()
    private var fragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
        initObserver()
    }

    private fun initObserver() {
        ShepherdApp.pauseAppLiveData.observe(this, Observer {
            if (it != null && it) {
                if (!Prefs.with(applicationContext)?.getString(Const.USER_TOKEN).isNullOrEmpty())
                    viewModel.pauseAppLogOut()
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, T::class.java).apply(block))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    inline fun <reified T : Activity> Context.startActivityWithFinish(block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, T::class.java).apply(block))
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    inline fun <reified T : Activity> Context.startActivityWithFinishAffinity(block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, T::class.java).apply(block))
        finishAffinity()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }

    fun showLoading(message: String?) {
        ProgressBarDialog.showProgressBar(this, message)
    }

    fun navigateToLogout(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("source", "base")
        startActivity(intent)
    }

    fun hideLoading() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                ProgressBarDialog.dismissProgressDialog()
            }, 500)
        }
    }

    fun openImagePicker() {
        val intent = Lassi(this)
            .with(LassiOption.CAMERA_AND_GALLERY)
            .setMaxCount(1)
            .setGridSize(3)
            .setPlaceHolder(com.lassi.R.drawable.ic_image_placeholder)
            .setErrorDrawable(com.lassi.R.drawable.ic_image_placeholder)
            .setSelectionDrawable(com.lassi.R.drawable.ic_checked_media)
            .setStatusBarColor(R.color.colorPrimaryDark)
            .setToolbarColor(R.color.colorPrimary)
            .setToolbarResourceColor(android.R.color.white)
            .setProgressBarColor(R.color.colorAccent)
            // .setCropType(CropImageView.CropShape.RECTANGLE)
            //.setCropAspectRatio(1, 1)
            //.disableCrop()
            .setCompressionRation(10)
            .setMinFileSize(0)
            .setMaxFileSize(2000)
            .setSupportedFileTypes("jpg", "jpeg", "png", "webp", "gif")
            .enableFlip()
            .enableRotate()
            .build()
        receiveData.launch(intent)
    }

    private fun openDocPicker() {
        val intent = Lassi(this)
            .with(LassiOption.CAMERA_AND_GALLERY)
            .setMaxCount(1)
            .setGridSize(3)
            .setMediaType(MediaType.DOC) // MediaType : VIDEO IMAGE, AUDIO OR DOC
            .setPlaceHolder(com.lassi.R.drawable.ic_image_placeholder)
            .setErrorDrawable(com.lassi.R.drawable.ic_image_placeholder)
            .setSelectionDrawable(com.lassi.R.drawable.ic_checked_media)
            .setStatusBarColor(R.color.colorPrimaryDark)
            .setToolbarColor(R.color.colorPrimary)
            .setToolbarResourceColor(android.R.color.white)
            .setProgressBarColor(R.color.colorAccent)
            .setMinFileSize(0)
            .setMaxFileSize(5000)
            .setSupportedFileTypes("pdf", "doc", "docx")
            .build()
        receiveData.launch(intent)
    }


    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (!selectedMedia.isNullOrEmpty()) {
                    var file: File? = null
                    file = if (selectedMedia[0].path?.startsWith("content")!!) {
                        CommonFunctions.fileFromContentUri(
                            this@BaseActivity,
                            selectedMedia[0].path.toString().toUri()
                        )
                    } else {
                        File(selectedMedia[0].path!!)
                    }
                    selectedFile.value = file!!
                }
            }
        }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun checkPermission(): Boolean {
        val readStorageresult =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val writeStorageresult =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val cameraResult = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        )
        return readStorageresult == PackageManager.PERMISSION_GRANTED && writeStorageresult == PackageManager.PERMISSION_GRANTED && cameraResult == PackageManager.PERMISSION_GRANTED
    }

    // Check Notification Permission for Android 13 or above
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Check Google Fit Permissions
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkGoogleFitPermission(): Boolean {
        return /*ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.ACTIVITY_RECOGNITION,) == PackageManager.PERMISSION_GRANTED
                && */ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPostNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
            ),
            PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestGoogleFitPermission(fragment: VitalStatsFragment) {
        this.fragment = fragment
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
//                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.BODY_SENSORS
            ),
            PERMISSION_REQUEST_CODE_GOOGLE_FIT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (readAccepted && writeAccepted && cameraAccepted)
                    openImagePicker()
                else {
                    // showError(this,"Permission Denied, You cannot access Gallery data and Camera.")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(this)
                        val dialog = builder.apply {
                            setMessage("Permission Denied, You need to allow Gallery data and Camera permissions")
                            setPositiveButton("OK") { _, _ ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //open app setting to access
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri: Uri =
                                        Uri.fromParts(
                                            "package",
                                            applicationContext.packageName,
                                            null
                                        )
                                    intent.data = uri
                                    context.startActivity(intent)
                                }

                            }
                            setNegativeButton("Cancel") { _, _ ->

                            }
                        }.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                        return
                    }
                }
            }

            PERMISSION_REQUEST_CODE_GOOGLE_FIT -> if (grantResults.isNotEmpty()) {
//                val activityRecognitionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val bodySensorsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (/*activityRecognitionAccepted &&*/ bodySensorsAccepted)
                    if (fragment is VitalStatsFragment) {
                        (fragment as VitalStatsFragment).fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
                        return
                    } else {
                        // showError(this,"Permission Denied, You cannot access Gallery data and Camera.")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val builder = AlertDialog.Builder(this)
                            val dialog = builder.apply {
                                setMessage("Permission Denied, You need to allow Body Sensors Permissions")
                                setPositiveButton("OK") { _, _ ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        //open app setting to access
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        val uri: Uri =
                                            Uri.fromParts(
                                                "package",
                                                applicationContext.packageName,
                                                null
                                            )
                                        intent.data = uri
                                        context.startActivity(intent)
                                    }

                                }
                                setNegativeButton("Cancel") { _, _ ->

                                }
                            }.create()
                            dialog.show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                            return
                        }
                    }


            }

            PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty()) {
                    val postNotificationPermissionAccepted =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (postNotificationPermissionAccepted) {
                        return
                    } /*else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val builder = AlertDialog.Builder(this)
                            val dialog = builder.apply {
                                setMessage("Permission Denied, You need to allow Notification Permissions")
                                setPositiveButton("OK") { _, _ ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        //open app setting to access
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        val uri: Uri =
                                            Uri.fromParts(
                                                "package",
                                                applicationContext.packageName,
                                                null
                                            )
                                        intent.data = uri
                                        context.startActivity(intent)
                                    }

                                }
                                setNegativeButton("Cancel") { _, _ ->

                                }
                            }.create()
                            dialog.show()
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                            return
                        }
                    }*/
                }

            }
        }

    }


    fun EditText.isValidPassword(): Boolean {
        return Pattern
            .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!&^%$#@()=*/.+_-])(?=\\S+$).{8,}$")
            .matcher(this.checkString()).matches()
    }
}
