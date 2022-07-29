package com.shepherd.app.ui.component.lockBox

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
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
    private var selectedFileList: ArrayList<File>? = arrayListOf()
    var uploadedDocumentsUrl: ArrayList<String>? = arrayListOf()
    private var mDriveServiceHelper: DriveServiceHelper? = null

    companion object {
        private const val TAG = "UploadDrive"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }


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

                uploadedDocumentsUrl.isNullOrEmpty() -> {
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
        /* if (args.lockBoxType != null) {
             lockBoxTypes = args.lockBoxType
             lbtId = args.lockBoxType?.id
             fragmentAddNewLockBoxBinding.edtFileName.setText(lockBoxTypes?.name)
             fragmentAddNewLockBoxBinding.edtNote.setText(lockBoxTypes?.description)
         }*/

    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)
        observe(selectedFiles, ::handleSelectedFiles)

        // Observe the response of upload image api
        /* addNewLockBoxViewModel.uploadLockBoxDocResponseLiveData.observeEvent(this) {
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
         }*/

        // Observe the response of upload multiple images api
        addNewLockBoxViewModel.uploadMultipleLockBoxDocResponseLiveData.observeEvent(this) {
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
//                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    showSuccess(requireContext(), "Files uploaded successfully...")
                    uploadedDocumentsUrl = it.data.payload?.document
                    Log.d(TAG, "Uploaded lockbox docs url: $uploadedDocumentsUrl")
                    if (isValid) {
                        fileName = fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim()
                        fileNote = fragmentAddNewLockBoxBinding.edtNote.text.toString().trim()
                        uploadedDocumentsUrl?.let {
                            addNewLockBoxViewModel.addNewLockBox(
                                fileName,
                                fileNote,
                                it,
                                lbtId
                            )
                        }
                    }
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
//                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    showSuccess(requireContext(), "New LockBox created Successfully...")
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                    backPress()
                }
            }
        }

        // Observe the response of get all uploaded lock box document by loved one uuid api
//        addNewLockBoxViewModel.getUploadedLockBoxDocResponseLiveData.observeEvent(this) {
//            when (it) {
//                is DataResult.Failure -> {
//                    hideLoading()
////                    it.message?.let { showError(requireContext(), it.toString()) }
//                    Log.d(TAG, "Get Uploaded LockBox Document : ${it.message}")
//                    fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
//                    fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
//                        View.VISIBLE
//                }
//                is DataResult.Loading -> {
//                    showLoading("")
//                }
//                is DataResult.Success -> {
//                    hideLoading()
//                    lockBox = it.data.payload?.lockBox
//
//                    if (lockBox.isNullOrEmpty()) {
//                        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
//                        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
//                            View.VISIBLE
//                    } else {
//                        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.VISIBLE
//                        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
////                        lockBox?.let { it1 -> uploadedFilesAdapter?.addData(it1) }
//                    }
//
//                }
//            }
//        }

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

    private fun handleSelectedFiles(selectedFiles: ArrayList<File>?) {
        dialog?.dismiss()
        this.selectedFileList = selectedFiles
        if (!selectedFiles.isNullOrEmpty()) {
            Log.d(TAG, "handleSelectedFiles: $selectedFiles")
            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.VISIBLE
            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                View.GONE
            uploadedFilesAdapter?.addData(selectedFiles)
        } else {
            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                View.VISIBLE
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
                selectedFileList?.let { addNewLockBoxViewModel.uploadMultipleLockBoxDoc(it) }
                /* if (isValid) {
                     fileName = fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim()
                     fileNote = fragmentAddNewLockBoxBinding.edtNote.text.toString().trim()
                     uploadedDocumentsUrl?.let {
                         addNewLockBoxViewModel.addNewLockBox(
                             fileName,
                             fileNote,
                             it,
                             lbtId
                         )
                     }
                 }*/
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
            requestSignIn()
        }

        // Click Local Storage
        cvLocalStorage.setOnClickListener {
            openMultipleDocPicker()
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

    override fun onItemClick(file: File) {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.apply {
            setTitle("Delete Uploaded Lock Box Document")
            setMessage("Are you sure you want to remove the uploaded lock box doc?")
            setPositiveButton("Yes") { _, _ ->
//                lockBox.id?.let { addNewLockBoxViewModel.deleteUploadedLockBoxDoc(it) }
                uploadedFilesAdapter?.removeData(file)
            }
            setNegativeButton("No") { _, _ ->
            }
        }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    /*************Code for google drive **************/
    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(requireContext().applicationContext, signInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext().applicationContext)
        if (account != null) {
            client.signOut()
        }
        startActivityForResult(
            client.signInIntent,
            REQUEST_CODE_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData)
                } else {
                    showError(
                        requireContext().applicationContext,
                        "Unable to create this request try, again later!"
                    )
                    Log.e("catch_exception", "Request not accepted $resultCode")
                }
            }
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                val uri = resultData.data
                uri?.let {
                    var file: File? = null
                    file = if (uri.toString().startsWith("content")) {
                        CommonFunctions.fileFromContentUri(
                            requireContext().applicationContext,
                            uri.toString().toString().toUri()
                        )
                    } else {
                        File(uri.toString())
                    }
                    if (file != null && file.exists()) {
                        addNewLockBoxViewModel.imageFile = file
                        if (selectedFileList!!.size > 5) {
                            showInfo(
                                requireContext().applicationContext,
                                getString(R.string.upload_five_documents)
                            )
                        } else {
                            selectedFileList!!.add(file)
                            if (selectedFileList!!.size > 0) {
                                Log.d(TAG, "handleSelectedFiles: $selectedFiles")
                                fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility =
                                    View.VISIBLE
                                fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                    View.GONE
                                uploadedFilesAdapter?.addData(selectedFileList!!)
                            } else {
                                fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
                                fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                    requireContext().applicationContext, setOf(DriveScopes.DRIVE_FILE)
                )
                credential.setSelectedAccount(googleAccount.account)
                val googleDriveService: Drive = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("Drive API Migration")
                    .build()

                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
                openFilePicker()
            }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    TAG,
                    "Unable to sign in.",
                    exception
                )
            }
    }

    private fun openFilePicker() {
        if (mDriveServiceHelper != null) {
            val pickerIntent: Intent = mDriveServiceHelper!!.createFilePickerIntent()
//            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT)
            selectImagesActivityResult.launch(pickerIntent)
        }
    }

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val uri: Uri? = data.clipData?.getItemAt(i)?.uri
                        uri?.let {
                            var file: File? = null
                            file = if (uri.toString().startsWith("content")) {
                                CommonFunctions.fileFromContentUri(
                                    requireContext().applicationContext,
                                    uri.toString().toUri()
                                )
                            } else {
                                File(uri.toString())
                            }
                            selectedFileList!!.add(file)

                        }
                       // selectedFileList!!.add(file)
                        if (selectedFileList!!.size > 0) {
                            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility =
                                View.VISIBLE
                            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.GONE
                            uploadedFilesAdapter?.addData(selectedFileList!!)
                        } else {
                            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
                            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.VISIBLE
                        }
                    }

                }
                //If single image selected
                else if (data?.data != null) {
                    val uri: Uri? = data.data

                    uri?.let {
                        var file: File? = null
                        file = if (uri.toString().startsWith("content")) {
                            CommonFunctions.fileFromContentUri(
                                requireContext().applicationContext,
                                uri.toString().toString().toUri()
                            )
                        } else {
                            File(uri.toString())
                        }
                        selectedFileList!!.add(file!!)
                        if (selectedFileList!!.size > 0) {
                            Log.d(TAG, "handleSelectedFiles: $selectedFiles")
                            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility =
                                View.VISIBLE
                            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.GONE
                            uploadedFilesAdapter?.addData(selectedFileList!!)
                        } else {
                            fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
                            fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.VISIBLE
                        }
                    }
                }
            }
        }

}

