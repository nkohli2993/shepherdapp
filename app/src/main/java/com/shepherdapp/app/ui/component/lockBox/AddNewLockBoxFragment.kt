package com.shepherdapp.app.ui.component.lockBox

import CommonFunctions
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.data.dto.med_list.MedListData
import com.shepherdapp.app.data.dto.med_list.Medlist
import com.shepherdapp.app.databinding.FragmentAddNewLockBoxBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addNewMedication.AddNewMedicationFragmentDirections
import com.shepherdapp.app.ui.component.lockBox.adapter.DocumentAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.LockBoxUsersAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.UploadedLockBoxFilesAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.AddNewLockBoxViewModel
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
    private var limit: Int = 20
    private var uploadedFilesAdapter: UploadedLockBoxFilesAdapter? = null
    private var selectedFileList: ArrayList<File>? = arrayListOf()
    private var uploadedDocumentsUrl: ArrayList<String>? = arrayListOf()
    private var usersList: ArrayList<CareTeamModel>? = arrayListOf()
    private var usersUUID: ArrayList<String>? = arrayListOf()
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var lockBoxTypes: ArrayList<LockBoxTypes> = arrayListOf()
    private var documentAdapter: DocumentAdapter? = null
    private var lockBoxUsersAdapter: LockBoxUsersAdapter? = null
    private var selectedDocumentId: String? = null
    private var selectedPosition: Int? = null

    private val args: AddNewLockBoxFragmentArgs by navArgs()

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim().isEmpty() -> {
                    fragmentAddNewLockBoxBinding.edtFileName.error =
                        getString(R.string.enter_file_name)
                    fragmentAddNewLockBoxBinding.edtFileName.requestFocus()
                }
                selectedDocumentId == null || selectedDocumentId == "-1" -> {
                    showInfo(requireContext(), getString(R.string.please_select_document_type))
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

    override fun onResume() {
        super.onResume()
        if (selectedFileList != null && selectedFileList!!.size > 0) {
            setFileViewVisible()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        fragmentAddNewLockBoxBinding.listener = this
        addNewLockBoxViewModel.getAllLockBoxTypes(pageNumber, limit, true)
        setUploadedFilesAdapter()

        fragmentAddNewLockBoxBinding.documentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedDocumentId = lockBoxTypes[p2].id.toString()
                    selectedPosition = p2
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        // Get data back from the launched fragment
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<CareTeamModel>>(
            "userList"
        )?.observe(viewLifecycleOwner) { users ->
            usersList = users
            Log.d(TAG, "initViewBinding: userList is $usersList")
            Log.d(TAG, "initViewBinding: userList size is ${usersList?.size}")

            usersUUID = usersList?.map {
                it.user_id
            } as ArrayList<String>

            setLockBoxUsersAdapter()
            if (selectedFileList != null && selectedFileList!!.size > 0) {
                setFileViewVisible()
            }
            uploadedFilesAdapter?.addData(selectedFileList!!)

            if (lockBoxTypes.size > 0 && selectedPosition != null) {
                fragmentAddNewLockBoxBinding.documentSpinner.setSelection(selectedPosition!!)
            }
        }
    }
    private fun openMemberDetails(navigateEvent: SingleEvent<String>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_to_assigneeUsersFragment, bundleOf("assignee_user_lockBox" to usersList))
        }
    }


    override fun observeViewModel() {
        observeEvent(selectedFile, ::handleSelectedImage)

        observe(addNewLockBoxViewModel.selectedUserLiveData, ::openMemberDetails)
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
                    showSuccess(requireContext(), getString(R.string.files_uploaded_successfully))
                    uploadedDocumentsUrl = it.data.payload?.document
                    Log.d(TAG, "Uploaded lockbox docs url: $uploadedDocumentsUrl")
                    fileName = fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim()
                    fileNote = if (fragmentAddNewLockBoxBinding.edtNote.text.isNullOrEmpty()) {
                        null
                    } else {
                        fragmentAddNewLockBoxBinding.edtNote.text.toString().trim()
                    }
                    uploadedDocumentsUrl?.let { list ->
                        addNewLockBoxViewModel.addNewLockBox(
                            fileName,
                            fileNote,
                            list,
                            selectedDocumentId?.toInt(),
                            usersUUID
                        )
                    }
                }
            }
        }

        // Observe the response of add new lock box api
        addNewLockBoxViewModel.addNewLockBoxResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), getString(R.string.document_added_successfully))
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                    backPress()
                }
            }
        }

        // Observe the response of delete uploaded lock box document
        addNewLockBoxViewModel.deleteUploadedLockBoxDocResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), result.data.message.toString())

                    // Reload the uploaded documents
                    addNewLockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(
                        pageNumber,
                        limit
                    )
                }
            }
        }

        //Observe the response of list of document type
        addNewLockBoxViewModel.lockBoxTypeResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()

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
                    lockBoxTypes.add(
                        0,
                        LockBoxTypes(
                            id = -1,
                            name = getString(R.string.selected_recommended_document_type)
                        )
                    )
                    if (lockBoxTypes.isEmpty()) return@observeEvent
                    // show types in dropdown
                    documentAdapter =
                        DocumentAdapter(
                            requireContext(),
                            R.layout.vehicle_spinner_drop_view_item,
                            lockBoxTypes
                        )
                    fragmentAddNewLockBoxBinding.documentSpinner.adapter = documentAdapter

                    // Get selected
                    val lBType = args.lockBoxType
                    Log.d(TAG, "lockBoxType : $lBType ")


                    val index = lockBoxTypes.indexOfFirst {
                        it.id == lBType?.id
                    }

                    fragmentAddNewLockBoxBinding.documentSpinner.setSelection(index)

                    if (lockBoxTypes.size > 0 && selectedPosition != null) {
                        fragmentAddNewLockBoxBinding.documentSpinner.setSelection(selectedPosition!!)
                    }

                    // Set Document name if lockBox slug is not other
                    /*if (!lBType?.slug.equals("other"))
                        fragmentAddNewLockBoxBinding.edtFileName.setText(lBType?.name)
*/
                }
            }
        }

    }

    private fun handleSelectedFiles(selectedFiles: SingleEvent<ArrayList<File>>) {
        selectedFiles.getContentIfNotHandled().let {
            dialog?.dismiss()
            if (it != null) {
                if (!it?.isEmpty()!!) {
                    selectedFileList!!.addAll(it)
                    setFileViewVisible()
                    uploadedFilesAdapter?.addData(it)
                } else {
                    setFileViewUnvisible()
                }
            }
        }

    }

    private fun handleSelectedImage(singleEvent: SingleEvent<File>) {
        singleEvent.getContentIfNotHandled().let {
            if (it != null && it.exists()) {
                addNewLockBoxViewModel.imageFile = it
                addNewLockBoxViewModel.uploadLockBoxDoc(it)
            }

        }
    }


    private fun setUploadedFilesAdapter() {
        uploadedFilesAdapter = UploadedLockBoxFilesAdapter(addNewLockBoxViewModel)
        fragmentAddNewLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter
        uploadedFilesAdapter?.setClickListener(this)

    }

    private fun setLockBoxUsersAdapter() {
        lockBoxUsersAdapter = usersList?.let { LockBoxUsersAdapter(it,addNewLockBoxViewModel) }
        fragmentAddNewLockBoxBinding.rvUsers.adapter = lockBoxUsersAdapter

        if (usersList?.size!! > 5) {
            fragmentAddNewLockBoxBinding.txtMore.visibility = View.VISIBLE
            val moreUser = usersList?.size!! - 5
            fragmentAddNewLockBoxBinding.txtMore.text = "+ $moreUser More"
        } else {
            fragmentAddNewLockBoxBinding.txtMore.visibility = View.GONE
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChooseFile -> {
                showChooseFileDialog()
            }

            R.id.btnDone -> {
                if (isValid) {
                    fileName = fragmentAddNewLockBoxBinding.edtFileName.text.toString().trim()
                    fileNote = if (fragmentAddNewLockBoxBinding.edtNote.text.isNullOrEmpty()) {
                        null
                    } else {
                        fragmentAddNewLockBoxBinding.edtNote.text.toString().trim()
                    }
                    if (selectedFileList.isNullOrEmpty()) {
                        if (usersList.isNullOrEmpty()) {
                            showError(
                                requireContext(),
                                getString(R.string.please_select_at_least_one_user)
                            )

                        } else
                            addNewLockBoxViewModel.addNewLockBox(
                                fileName,
                                fileNote,
                                null,
                                selectedDocumentId?.toInt(),
                                usersUUID
                            )

                    } else {
                        var isFileFormatValid = false
                        for (i in selectedFileList?.indices!!) {
                            isFileFormatValid =
                                FileValidator().validate(selectedFileList!![i].toString())

                            if (!isFileFormatValid) break
                        }
                        if (!isFileFormatValid) {
                            showError(
                                requireContext(),
                                getString(R.string.only_jpg_png_word_text_file_can_be_uploaded)
                            )
                        } else if (selectedFileList!!.size > 5) {
                            showError(
                                requireContext(),
                                getString(R.string.you_can_upload_at_max_five_file)
                            )
                        } else if (usersList.isNullOrEmpty()) {
                            showError(
                                requireContext(),
                                getString(R.string.please_select_at_least_one_user)
                            )

                            
                        } else {
                            selectedFileList?.let {
                                addNewLockBoxViewModel.uploadMultipleLockBoxDoc(
                                    it
                                )
                            }
                        }
                    }
                }
            }
            R.id.ivBack -> {
                backPress()
            }
            R.id.imgSelectUsers -> {
                Log.d(TAG, "Select Users : clicked")
                findNavController().navigate(R.id.action_addNewLockBoxFragment_to_selectUsersFragment)
            }
        }
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
            // For Android 13 or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                openMultipleDocPicker()
            } else {
                //call back after permission granted
                if (!checkPermission()) {
                    requestPermission(300)
                } else {
                    openMultipleDocPicker()
                }
            }

            //call back after permission granted
            /* if (!checkPermission()) {
                 requestPermission(300)
             } else {
                 openMultipleDocPicker()
             }*/
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

    override fun onItemClick(file: File, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.apply {
            setTitle(getString(R.string.delete_upload_document))
            setMessage(getString(R.string.are_you_sure_u_want_to_delete_uploaded_document))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                selectedFileList?.removeAt(position)
                uploadedFilesAdapter?.removeData(position)
                selectedFileShow()
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->
            }
        }.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    private fun selectedFileShow() {
        if ((selectedFileList?.size ?: 0) <= 0) {
            setFileViewUnvisible()
        } else {
            setFileViewVisible()
        }
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
                            selectedFileList!!.add(file)
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
                        selectedFileList!!.addAll(driveSelectedFileList)
                        uploadedFilesAdapter?.addData(driveSelectedFileList)
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
                        selectedFileList!!.addAll(fileSelectedUpload)
                        if (selectedFileList!!.size > 0) {
                            setFileViewVisible()
                            uploadedFilesAdapter?.addData(fileSelectedUpload)
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
        fragmentAddNewLockBoxBinding.txtUploadedFiles.visibility = View.GONE
        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility = View.GONE
        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
            View.GONE
    }

    private fun setFileViewVisible() {
        fragmentAddNewLockBoxBinding.txtUploadedFiles.visibility = View.VISIBLE
        fragmentAddNewLockBoxBinding.rvUploadedFiles.visibility =
            View.VISIBLE
        fragmentAddNewLockBoxBinding.txtNoUploadedLockBoxFile.visibility =
            View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

