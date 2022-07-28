package com.shepherd.app.ui.component.lockBox

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherd.app.databinding.FragmentAddNewLockBoxBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBox.adapter.UploadedLockBoxFilesAdapter
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddNewLockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * Created by Deepak Rattan on 22-07-22
 */
@AndroidEntryPoint
class AddNewLockBoxFragment : BaseFragment<FragmentAddNewLockBoxBinding>(),
    View.OnClickListener, UploadedLockBoxFilesAdapter.OnItemClickListener {

    private val addNewLockBoxViewModel: AddNewLockBoxViewModel by viewModels()
    private lateinit var fragmentAddNewLockBoxBinding: FragmentAddNewLockBoxBinding
    private var uploadedLockBoxDocUrl: String? = null
    private var fileName: String? = null
    private var fileNote: String? = null
    private val TAG = "AddNewLockBoxFragment"
    private var dialog: Dialog? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10
    var lockBox: ArrayList<LockBox>? = arrayListOf()
    var uploadedFilesAdapter: UploadedLockBoxFilesAdapter? = null
    private val args: AddNewLockBoxFragmentArgs by navArgs()
    private var lockBoxTypes: LockBoxTypes? = null
    private var lbtId: Int? = null


    private val isValid: Boolean
        get() {
            when {
                fragmentAddNewLockBoxBinding.edtFileName.text.toString().isEmpty() -> {
                    fragmentAddNewLockBoxBinding.edtFileName.error =
                        getString(R.string.enter_file_name)
                    fragmentAddNewLockBoxBinding.edtFileName.requestFocus()
                }
                fragmentAddNewLockBoxBinding.edtNote.text.toString().isEmpty() -> {
                    fragmentAddNewLockBoxBinding.edtNote.error = getString(R.string.enter_note)
                }

                uploadedLockBoxDocUrl.isNullOrEmpty() -> {
                    showInfo(requireContext(), "Please upload file...")
                }
                else -> {
                    return true
                }
            }
            return false
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewLockBoxBinding =
            FragmentAddNewLockBoxBinding.inflate(inflater, container, false)

        addNewLockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)

        return fragmentAddNewLockBoxBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewLockBoxBinding.listener = this
        setUploadedFilesAdapter()
        if (args.lockBoxType != null) {
            lockBoxTypes = args.lockBoxType
            lbtId = args.lockBoxType?.id
            fragmentAddNewLockBoxBinding.edtFileName.setText(lockBoxTypes?.name)
            fragmentAddNewLockBoxBinding.edtNote.setText(lockBoxTypes?.description)
        }

    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        // Observe the response of upload image api
        addNewLockBoxViewModel.uploadLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    dialog?.dismiss()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    dialog?.dismiss()
                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    uploadedLockBoxDocUrl = it.data.payload.document
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                }
            }
        }

        // Observe the response of add new lock box api
        addNewLockBoxViewModel.addNewLockBoxResponseLiveData.observeEvent(this) {
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
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                    backPress()
                }
            }
        }

        // Observe the response of get all uploaded lock box document by loved one uuid api
        addNewLockBoxViewModel.getUploadedLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
//                    it.message?.let { showError(requireContext(), it.toString()) }
                    Log.d(TAG, "Get Uploaded LockBox Document : ${it.message}")
                    fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
                    fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                        View.VISIBLE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    lockBox = it.data.payload?.lockBox

                    if (lockBox.isNullOrEmpty()) {
                        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
                        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.VISIBLE
                    } else {
                        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.VISIBLE
                        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
                        lockBox?.let { it1 -> uploadedFilesAdapter?.addData(it1) }
                    }

                }
            }
        }

        // Observe the response of delete uploaded lock box document
        addNewLockBoxViewModel.deleteUploadedLockBoxDocResponseLiveData.observeEvent(this) {
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
                    showSuccess(requireContext(), it.data.message.toString())

                    // Reload the uploaded documents
                    addNewLockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(
                        pageNumber,
                        limit
                    )
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
        uploadedFilesAdapter = UploadedLockBoxFilesAdapter(addNewLockBoxViewModel)
        fragmentAddNewLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter
        uploadedFilesAdapter?.setClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChooseFile -> {
                showChooseFileDialog()
            }
            R.id.btnDone -> {
                if (isValid) {
                    fileName = fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim()
                    fileNote = fragmentAddNewLockBoxBinding.edtNote.text.toString().trim()
                    addNewLockBoxViewModel.addNewLockBox(
                        fileName,
                        fileNote,
                        uploadedLockBoxDocUrl,
                        lbtId
                    )
                }
            }
            R.id.ivBack -> {
                backPress()
            }
        }
    }

    private fun showChooseFileDialog() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_choose_lockbox_file)
        val cvGoogleDrive = dialog?.findViewById(R.id.cvGoogleDrive) as CardView
        val cvLocalStorage = dialog?.findViewById(R.id.cvLocalStorage) as CardView
        val cancel = dialog?.findViewById(R.id.txtCancel) as TextView
        // Click Google Drive
        cvGoogleDrive.setOnClickListener {
            dialog?.dismiss()
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
            dialog?.dismiss()
            showToast("Cancel clicked")
        }
        dialog?.setCancelable(false)
        dialog?.show()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }

    override fun onItemClick(lockBox: LockBox) {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.apply {
            setTitle("Delete Uploaded Lock Box Document")
            setMessage("Are you sure you want to remove the uploaded lock box doc?")
            setPositiveButton("Yes") { _, _ ->
                lockBox.id?.let { addNewLockBoxViewModel.deleteUploadedLockBoxDoc(it) }
            }
            setNegativeButton("No") { _, _ ->
            }
        }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


}

