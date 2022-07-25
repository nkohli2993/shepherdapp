package com.app.shepherd.ui.component.lockBox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentAddNewLockBoxBinding
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.AddNewLockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * Created by Deepak Rattan on 22-07-22
 */
@AndroidEntryPoint
class AddNewLockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

    private val addNewLockBoxViewModel: AddNewLockBoxViewModel by viewModels()
    private lateinit var fragmentAddNewLockBoxBinding: FragmentAddNewLockBoxBinding
    private var uploadedLockBoxDocUrl: String? = null
    private val TAG = "AddNewLockBoxFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewLockBoxBinding =
            FragmentAddNewLockBoxBinding.inflate(inflater, container, false)

        return fragmentAddNewLockBoxBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewLockBoxBinding.listener = this

        setUploadedFilesAdapter()


    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        // Observe the response of upload image api
        addNewLockBoxViewModel.uploadLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    uploadedLockBoxDocUrl = it.data.payload.document
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                }
            }
        }
    }

    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            addNewLockBoxViewModel.imageFile = file
            addNewLockBoxViewModel.uploadLockBoxDoc(file)
        }
    }


    private fun setUploadedFilesAdapter() {
//        val uploadedFilesAdapter = UploadedFilesAdapter(lockBoxViewModel)
//        fragmentAddNewLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter

    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChooseFile -> {
                showChooseFileDialog()
            }
        }
    }

    private fun showChooseFileDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_choose_lockbox_file)
        val cvGoogleDrive = dialog.findViewById(R.id.cvGoogleDrive) as CardView
        val cvLocalStorage = dialog.findViewById(R.id.cvLocalStorage) as CardView
        val cancel = dialog.findViewById(R.id.txtCancel) as TextView
        // Click Google Drive
        cvGoogleDrive.setOnClickListener {
            dialog.dismiss()
            showToast("Google Drive Clicked")

            val intent = Intent(
                requireContext().applicationContext,
                UploadLockBoxDocumentActivity::class.java
            )
//        intent.putExtra(Const.LOVED_ONE_ARRAY, userLovedOneArrayList)
            startActivity(intent)
        }

        // Click Local Storage
        cvLocalStorage.setOnClickListener {
//            dialog.dismiss()
//            showToast("Local Storage Clicked")
            openDocPicker()
        }

        // Click Cancel
        cancel.setOnClickListener {
            dialog.dismiss()
            showToast("Cancel clicked")
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }


}

