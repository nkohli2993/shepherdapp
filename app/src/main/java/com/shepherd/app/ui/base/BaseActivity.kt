package com.shepherd.app.ui.base

import CommonFunctions
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.shepherd.app.R
import com.shepherd.app.ui.component.carePoints.CarePointsFragment
import com.shepherd.app.ui.component.carePoints.CarePointsFragmentDirections
import com.shepherd.app.ui.component.careTeamMembers.CareTeamMembersFragment
import com.shepherd.app.ui.component.lockBox.LockBoxFragment
import com.shepherd.app.ui.component.myMedList.MyMedListFragment
import com.shepherd.app.utils.ProgressBarDialog
import java.io.File
import androidx.navigation.fragment.findNavController
import com.shepherd.app.ui.component.dashboard.DashboardFragment

/**
 * Created by Sumit Kumar
 */


abstract class BaseActivity : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()
    var selectedFile: MutableLiveData<File> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
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
            .setSupportedFileTypes("jpg", "jpeg", "png", "webp", "gif", "pdf")
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
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        // to check navigation fragments
        val navHostFragment = supportFragmentManager.primaryNavigationFragment as NavHostFragment?
        val fragmentManager: FragmentManager = navHostFragment!!.childFragmentManager
        val fragment: Fragment = fragmentManager.getPrimaryNavigationFragment()!!
        when (fragment) {
            is CarePointsFragment, is MyMedListFragment, is LockBoxFragment, is CareTeamMembersFragment -> {
                navController.navigate(R.id.nav_dashboard)
            }
            is DashboardFragment -> {
                finishActivity()
            }
            else -> {
                super.onBackPressed()
            }

        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close

    }

}
