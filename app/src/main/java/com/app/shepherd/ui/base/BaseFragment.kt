package com.app.shepherd.ui.base

import CommonFunctions
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import java.io.File


abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()
    var selectedFile: MutableLiveData<File> = MutableLiveData()


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
                    selectedFile.value = file!!
                }
            }
        }

}