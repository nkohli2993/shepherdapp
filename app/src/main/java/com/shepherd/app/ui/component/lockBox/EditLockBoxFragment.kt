package com.shepherd.app.ui.component.lockBox

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
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
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.edit_lock_box.DocumentData
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherd.app.databinding.FragmentEditLockBoxBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBox.adapter.DocumentAdapter
import com.shepherd.app.ui.component.lockBox.adapter.UploadedDocumentAdapter
import com.shepherd.app.utils.FileValidator
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddNewLockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Nikita Kohli on 07-09-22
 */
@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class EditLockBoxFragment : BaseFragment<FragmentEditLockBoxBinding>(),
    View.OnClickListener, UploadedDocumentAdapter.OnItemClickListener {

    private val addNewLockBoxViewModel: AddNewLockBoxViewModel by viewModels()
    private lateinit var fragmentEditLockBoxBinding: FragmentEditLockBoxBinding
    private var fileName: String? = null
    private var fileNote: String? = null
    private val TAG = "EditNewLockBoxFragment"
    private var dialog: Dialog? = null
    private var pageNumber: Int = 1
    private var limit: Int = 20
    private var uploadedFilesAdapter: UploadedDocumentAdapter? = null
    private var selectedFileList: ArrayList<DocumentData>? = arrayListOf()
    private var uploadedDocumentsUrl: ArrayList<String>? = arrayListOf()
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var lockBoxTypes: ArrayList<LockBoxTypes> = arrayListOf()
    private var documentAdapter: DocumentAdapter? = null
    private var selectedDocumentId: String? = null
    private var lockBoxId: Int? = null
    private val args: EditLockBoxFragmentArgs by navArgs()
    private var uploadedFiles: ArrayList<DocumentData> = arrayListOf()
    private var deletedFileIds: ArrayList<Int> = arrayListOf()

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditLockBoxBinding = FragmentEditLockBoxBinding.inflate(inflater, container, false)
        return fragmentEditLockBoxBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)
        observe(selectedFiles, ::handleSelectedFiles)

        // Observe the response of upload multiple images api
        addNewLockBoxViewModel.uploadMultipleLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    dialog?.dismiss()
                    it.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    dialog?.dismiss()
                    showSuccess(requireContext(), "Files uploaded successfully...")
                    uploadedDocumentsUrl = it.data.payload?.document
                    Log.d(TAG, "Uploaded lockbox docs url: $uploadedDocumentsUrl")
                    fileName = fragmentEditLockBoxBinding.edtFileName.text.toString().trim()
                    fileNote = fragmentEditLockBoxBinding.edtNote.text.toString().trim()


                    uploadedDocumentsUrl?.let { list ->
                        addNewLockBoxViewModel.editNewLockBox(
                            fileName,
                            fileNote,
                            list,
                            selectedDocumentId?.toInt(), lockBoxId!!
                        )
                    }
                }
            }
        }

        //Observe the response of list of document type
        addNewLockBoxViewModel.lockBoxTypeResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    //  hideLoading()
                    showError(requireContext(), it.message.toString())
                    addNewLockBoxViewModel.getDetailLockBox(lockBoxId!!)
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    //  hideLoading()

                    if (it.data.payload?.lockBoxTypes != null) {
                        lockBoxTypes.clear()
                        if (it.data.payload?.lockBoxTypes != null || it.data.payload?.lockBoxTypes!!.size > 0) {
                            for (i in it.data.payload?.lockBoxTypes!!) {
                                if ((i.lockbox == null || i.lockbox.size <= 0)) {
                                    lockBoxTypes.add(i)
                                }
                                if (i.name?.lowercase() == "other" && i.lockbox.size > 0) {
                                    lockBoxTypes.add(i)
                                }
                            }
                        }
                    }
                    lockBoxTypes.add(0, LockBoxTypes(id = -1, name = "Select Document Type"))
                    if (lockBoxTypes.isEmpty()) return@observeEvent
                    // show types in dropdown
                    documentAdapter =
                        DocumentAdapter(
                            requireContext(),
                            R.layout.vehicle_spinner_drop_view_item,
                            lockBoxTypes
                        )
                    fragmentEditLockBoxBinding.documentSpinner.adapter = documentAdapter
                    addNewLockBoxViewModel.getDetailLockBox(lockBoxId!!)
                }
            }
        }

        addNewLockBoxViewModel.getDetailLockBoxResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
//                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    fragmentEditLockBoxBinding.let {
                        result.data.payload.let { payload ->
                            it.edtFileName.setText(payload?.name)
                            it.edtNote.setText(payload?.note)
                            // set document type selection for added document type
                            var position = 0
                            for (i in lockBoxTypes.indices) {
                                if (lockBoxTypes[i].id == result.data.payload?.lbtId) {
                                    position = i
                                }
                            }
                            it.documentSpinner.setSelection(position)
                            //set file added adapter
                            if (payload?.documents != null || payload?.documents!!.size > 0) {
                                for (i in payload.documents.indices) {
                                    uploadedFiles.add(
                                        DocumentData(
                                            i,
                                            payload.documents[i].url!!,
                                            SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().time)
                                        )
                                    )
                                }

                            }
                            uploadedFilesAdapter?.addData(uploadedFiles)
                        }

                    }

                }
            }
        }

    }

    private fun handleSelectedFiles(selectedFiles: ArrayList<File>?) {
        dialog?.dismiss()
        val uploadSelectedFiles: ArrayList<DocumentData> = arrayListOf()
        for (i in selectedFiles!!) {
            uploadSelectedFiles.add(
                DocumentData(
                    -1,
                    i.toString(),
                    SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().time), true
                )
            )

        }
        selectedFileList!!.addAll(
            uploadSelectedFiles
        )
        uploadedFiles.addAll(selectedFileList!!)
        if (!selectedFiles.isNullOrEmpty()) {
            setFileViewVisible()
            uploadedFilesAdapter?.addData(uploadSelectedFiles)
        } else {
            setFileViewUnvisible()
        }
    }


    override fun initViewBinding() {
        addNewLockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
        fragmentEditLockBoxBinding.listener = this
        lockBoxId = args.id!!.toInt()
        addNewLockBoxViewModel.getAllLockBoxTypes(pageNumber, limit, true)
        setUploadedFilesAdapter()
        fragmentEditLockBoxBinding.edtNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
        fragmentEditLockBoxBinding.documentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedDocumentId = lockBoxTypes[p2].id.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_lock_box
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChooseFile -> {
                showChooseFileDialog()
            }
            R.id.btnCancel -> {
                backPress()
            }
            R.id.btnSaveChanges -> {
                if (isValid) {
                    var isFileFormatValid = false
                    for (i in uploadedFiles.indices) {
                        isFileFormatValid =
                            FileValidator().validate(uploadedFiles[i].filePath)

                        if (!isFileFormatValid) break
                    }
                    if (!isFileFormatValid) {
                        showError(
                            requireContext(),
                            getString(R.string.only_jpg_png_word_text_file_can_be_uploaded)
                        )
                    } else if (uploadedFiles.size > 5) {
                        showError(
                            requireContext(),
                            getString(R.string.you_can_upload_at_max_five_file)
                        )
                    } else {
                        Log.e(TAG, "deletedFiles ${deletedFileIds} added : $selectedFileList")
                        //selectedFileList?.let { addNewLockBoxViewModel.uploadMultipleLockBoxDoc(it) }
                    }
                }
            }
            R.id.ivBack -> {
                backPress()
            }
        }
    }

    private fun selectedFileShow() {
        if ((uploadedFiles.size ?: 0) <= 0) {
            setFileViewUnvisible()
        } else {
            setFileViewVisible()
        }
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentEditLockBoxBinding.edtFileName.text.toString().trim().isEmpty() -> {
                    fragmentEditLockBoxBinding.edtFileName.error =
                        getString(R.string.enter_file_name)
                    fragmentEditLockBoxBinding.edtFileName.requestFocus()
                }
                selectedDocumentId == null || selectedDocumentId == "-1" -> {
                    showInfo(requireContext(), getString(R.string.please_select_document_type))
                }
                fragmentEditLockBoxBinding.edtNote.text.toString().trim().isEmpty() -> {
                    fragmentEditLockBoxBinding.edtNote.error = getString(R.string.enter_note)
                }

                uploadedFiles.isNullOrEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_upload_file))
                }
                else -> {
                    return true
                }
            }
            return false
        }


    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            addNewLockBoxViewModel.imageFile = file
            addNewLockBoxViewModel.uploadLockBoxDoc(file)
        }
    }


    private fun setUploadedFilesAdapter() {
        uploadedFilesAdapter = UploadedDocumentAdapter()
        fragmentEditLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter
        uploadedFilesAdapter?.setClickListener(this)

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
                }
            }
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                val uri = resultData.data
                uri?.let {
                    val file: File = if (uri.toString().startsWith("content")) {
                        CommonFunctions.fileFromContentUri(
                            requireContext().applicationContext,
                            uri.toString().toUri()
                        )
                    } else {
                        File(uri.toString())
                    }
                    if (file.exists()) {
                        addNewLockBoxViewModel.imageFile = file
                        if (selectedFileList!!.size > 5) {
                            showInfo(
                                requireContext().applicationContext,
                                getString(R.string.upload_five_documents)
                            )
                        } else {
                            selectedFileList!!.add(
                                DocumentData(
                                    -1,
                                    file.toString(),
                                    SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().time),
                                    true
                                )
                            )
                            uploadedFiles.addAll(selectedFileList!!)
                            if (selectedFileList!!.size > 0) {
                                Log.d(TAG, "handleSelectedFiles: $selectedFiles")
                                setFileViewVisible()
                                uploadedFilesAdapter?.addData(selectedFileList!!)
                            } else {
                                setFileViewUnvisible()
                            }
                            selectedFileShow()
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
            selectImagesActivityResult.launch(pickerIntent)
        }
    }

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                showLoading("Fetching file, Please wait")
                val data: Intent? = result.data
                val driveSelectedFileList: ArrayList<File> = arrayListOf()
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        val uri: Uri? = data.clipData?.getItemAt(i)?.uri
                        uri?.let {
                            val file = if (uri.toString().startsWith("content")) {
                                CommonFunctions.fileFromContentUri(
                                    requireContext().applicationContext,
                                    uri.toString().toUri()
                                )
                            } else {
                                File(uri.toString())
                            }
                            driveSelectedFileList.add(file)

                        }
                    }
                    if (driveSelectedFileList.size > 0) {
                        setFileViewVisible()
                        // selectedFileList!!.addAll(driveSelectedFileList)
//                        uploadedFilesAdapter?.addData(driveSelectedFileList)
                        selectedFileShow()
                    } else {
                        setFileViewUnvisible()
                    }
                    selectedFileShow()
                    hideLoading()

                }
                //If single image selected
                else if (data?.data != null) {
                    val uri: Uri? = data.data

                    uri?.let {
                        val file = if (uri.toString().startsWith("content")) {
                            CommonFunctions.fileFromContentUri(
                                requireContext().applicationContext,
                                uri.toString().toUri()
                            )
                        } else {
                            File(uri.toString())
                        }
                        val fileSelectedUpload: ArrayList<File> = arrayListOf()
                        fileSelectedUpload.add(file)
                        // selectedFileList!!.addAll(fileSelectedUpload)
                        if (selectedFileList!!.size > 0) {
                            setFileViewVisible()
//                            uploadedFilesAdapter?.addData(fileSelectedUpload)
                        } else {
                            setFileViewUnvisible()
                        }
                        selectedFileShow()
                        hideLoading()
                    }
                }
            }
        }

    private fun setFileViewUnvisible() {
        fragmentEditLockBoxBinding.txtUploadedFiles.visibility = View.GONE
        fragmentEditLockBoxBinding.rvUploadedFiles.visibility = View.GONE
        fragmentEditLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
            View.GONE
    }

    private fun setFileViewVisible() {
        fragmentEditLockBoxBinding.txtUploadedFiles.visibility = View.VISIBLE
        fragmentEditLockBoxBinding.rvUploadedFiles.visibility =
            View.VISIBLE
        fragmentEditLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
            View.GONE
    }

    private fun showChooseFileDialog() {
        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
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
            //call back after permission granted
            if (!checkPermission()) {
                requestPermission(300)
            } else {
                openMultipleDocPicker()
            }
        }

        // Click Cancel
        cancel.setOnClickListener {
            dialog?.dismiss()
            showToast("Cancel clicked")
        }
        dialog?.setCancelable(false)
        dialog?.show()
    }

    override fun onItemClick(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.apply {
            setTitle("Delete Uploaded Lock Box Document")
            setMessage("Are you sure you want to remove the uploaded lock box doc?")
            setPositiveButton("Yes") { _, _ ->
                if (!uploadedFiles[position].newAdded) {
                    deletedFileIds.add(uploadedFiles[position].id)
                }

                uploadedFiles.removeAt(position)
                uploadedFilesAdapter?.removeData(position)
                //selectedFileList?.removeAt(position)
                selectedFileShow()
            }
            setNegativeButton("No") { _, _ ->
            }
        }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


}