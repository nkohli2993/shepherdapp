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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.shepherdapp.app.R
import com.shepherdapp.app.utils.SingleEvent
import java.io.File


abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()
    var selectedFile: MutableLiveData<SingleEvent<File>> = MutableLiveData()
    var selectedFiles: MutableLiveData<SingleEvent<ArrayList<File>>> = MutableLiveData()

    private var PERMISSION_REQUEST_CODE = 200
    private var PERMISSION_REQUEST_CODE_MUTLIPLE = 300
    var customerDetailsDialog: BottomSheetDialog? = null
    var reportUserDialog: BottomSheetDialog? = null
    open lateinit var binding: DB

    var isLoaded = false
    var reportProviderClick: MutableLiveData<Boolean> = MutableLiveData()
    var reportType = 0
    var reportComment = ""
    var reportCustomerId = ""
    private lateinit var baseActivity: BaseActivity

    @LayoutRes
    abstract fun getLayoutRes(): Int

    private fun init(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(inflater, container)
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewBinding()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        isLoaded = true
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.baseActivity = context
        }
    }

    fun hideKeyBoard(input: View?) {
        input?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
        }
    }

    fun showLoading(message: String?) {
        try {
            baseActivity.showLoading(message)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun hideLoading() {
        try {
            baseActivity.hideLoading()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    open fun backPress() {
        findNavController().popBackStack()
    }

    inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, T::class.java).apply(block))
    }

    inline fun <reified T : Activity> Context.startActivityWithFinish(block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, T::class.java).apply(block))
        activity?.finish()
    }

    inline fun <reified T : Activity> Context.startActivityWithDelay(crossinline block: Intent.() -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, T::class.java).apply(block))
        }, 1000)
    }

    inline fun <reified T : Activity> Context.startActivityWithFinishDelay(crossinline block: Intent.() -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, T::class.java).apply(block))
            activity?.finish()
        }, 1000)
    }

    fun finishWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.finish()
        }, 1000)

    }

    private var toast: Toast? = null
    fun showToast(message: String?) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun openDocPicker() {
        val intent = Lassi(requireContext())
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
            .setSupportedFileTypes("pdf", "doc", "docx", "jpg", "jpeg", "png", "gif")
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
                            requireContext(),
                            selectedMedia[0].path.toString().toUri()
                        )
                    } else {
                        File(selectedMedia[0].path!!)
                    }
                    selectedFile.value = SingleEvent(file!!)
                }
            }
        }

    fun openMultipleDocPicker() {
        val intent = Lassi(requireContext())
            .with(LassiOption.CAMERA_AND_GALLERY)
            .setMaxCount(5)
            .setGridSize(3)
            .setMediaType(MediaType.FILE_TYPE_WITH_SYSTEM_VIEW) // MediaType : VIDEO IMAGE, AUDIO OR DOC
            .setPlaceHolder(com.lassi.R.drawable.ic_image_placeholder)
            .setErrorDrawable(com.lassi.R.drawable.ic_image_placeholder)
            .setSelectionDrawable(com.lassi.R.drawable.ic_checked_media)
            .setStatusBarColor(R.color.colorPrimaryDark)
            .setToolbarColor(R.color.colorPrimary)
            .setToolbarResourceColor(android.R.color.white)
            .setProgressBarColor(R.color.colorAccent)
            .setMinFileSize(0)
            .setMaxFileSize(5000)
            .setSupportedFileTypes("pdf", "doc", "docx", "jpg", "jpeg", "png", "gif")
            .build()
        receiveMultipleData.launch(intent)
    }

    private val receiveMultipleData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (!selectedMedia.isNullOrEmpty()) {
                    var file: File? = null
                    val files: ArrayList<File> = arrayListOf()
                    for (i in selectedMedia.indices) {
                        file = if (selectedMedia[i].path?.startsWith("content") == true) {
                            CommonFunctions.fileFromContentUri(
                                requireContext(),
                                selectedMedia[i].path.toString().toUri()
                            )
                        } else {
                            File(selectedMedia[i].path!!)

                        }
                        files.add(file)
                    }
                    Log.d("Base Fragment", "selected files :$files ")
                    Log.d("Base Fragment", "selected files size :${files.size} ")
                    selectedFiles.value = SingleEvent(files)
                }
            }
        }


    fun checkPermission(): Boolean {
        val readStorageresult =
            ContextCompat.checkSelfPermission(baseActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val writeStorageresult =
            ContextCompat.checkSelfPermission(baseActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val cameraResult = ContextCompat.checkSelfPermission(baseActivity,
            Manifest.permission.CAMERA
        )
        return readStorageresult == PackageManager.PERMISSION_GRANTED && writeStorageresult == PackageManager.PERMISSION_GRANTED && cameraResult == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permissionCode:Int) {
        ActivityCompat.requestPermissions(
            baseActivity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            permissionCode
        )
        PERMISSION_REQUEST_CODE = permissionCode
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
                if (readAccepted && writeAccepted  && cameraAccepted){
                    if(requestCode == 200){
                        openImagePicker()
                    }
                    else if(requestCode == 300){
                        openMultipleDocPicker()
                    }
                }

                else {
                    // showError(this,"Permission Denied, You cannot access Gallery data and Camera.")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(baseActivity)
                        val dialog = builder.apply {
                            setMessage("Permission Denied, You need to allow Gallery data and Camera permissions")
                            setPositiveButton("OK") { _, _ ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //open app setting to access
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri: Uri =
                                        Uri.fromParts("package", requireContext().packageName, null)
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
        }

    }

    fun openImagePicker() {
        val intent = Lassi(requireContext())
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


}