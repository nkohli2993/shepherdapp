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
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.*
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.Documents
import com.shepherdapp.app.data.dto.lock_box.edit_lock_box.DocumentData
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.databinding.FragmentEditLockBoxBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.lockBox.adapter.DocumentAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.LockBoxUsersAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.UploadedDocumentAdapter
import com.shepherdapp.app.utils.FileValidator
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.AddNewLockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Nikita Kohli on 07-09-22
 */
@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
class EditLockBoxFragment : BaseFragment<FragmentEditLockBoxBinding>(),
    View.OnClickListener, UploadedDocumentAdapter.OnItemClickListener {

    private val addNewLockBoxViewModel: AddNewLockBoxViewModel by viewModels()
    private var fragmentEditLockBoxBinding: FragmentEditLockBoxBinding? = null
    private var fileName: String? = null
    private var fileNote: String? = null
    private val TAG = "EditNewLockBoxFragment"
    private var dialog: Dialog? = null
    private var pageNumber: Int = 1
    private var limit: Int = 20
    private var uploadedFilesAdapter: UploadedDocumentAdapter? = null
    private var selectedFileList: ArrayList<DocumentData>? = arrayListOf()
    private var uploadedDocumentsUrl: ArrayList<String>? = arrayListOf()
    private var alreadyAdded: ArrayList<String> = arrayListOf()
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var lockBoxTypes: ArrayList<LockBoxTypes> = arrayListOf()
    private var documentAdapter: DocumentAdapter? = null
    private var selectedDocumentId: String? = null
    private var selectedPosition :Int? = null
    private var lockBoxId: Int? = null
    private val args: EditLockBoxFragmentArgs by navArgs()
    private var uploadedFiles: ArrayList<DocumentData> = arrayListOf()
    private var deletedSelectedDocs: ArrayList<Documents>? = arrayListOf()
    private var dateFormat = SimpleDateFormat("MMM dd, yyyy")
    private var isLoading = false
    private var isBinded = false
    private var lockBoxTypeId: Int? = null
    private var usersList: ArrayList<CareTeamModel>? = arrayListOf()
    private var usersListFromSelectUser: ArrayList<CareTeamModel>? = arrayListOf()
    private var usersUUID: ArrayList<String>? = arrayListOf()
    private var lockBoxUsersAdapter: LockBoxUsersAdapter? = null


    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fragmentEditLockBoxBinding == null) {
            fragmentEditLockBoxBinding =
                FragmentEditLockBoxBinding.inflate(inflater, container, false)
            isBinded = true
        }
        return fragmentEditLockBoxBinding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isBinded) {
            observeLifecycle()
            initBinding()
        }else{
            // Get data back from the launched fragment
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<CareTeamModel>>(
                "userList"
            )?.observe(viewLifecycleOwner) { users ->
                usersListFromSelectUser = users
                Log.d(TAG, "initViewBinding: userList is $usersList")
                Log.d(TAG, "initViewBinding: userList size is ${usersList?.size}")

                usersUUID = usersListFromSelectUser?.map {
                    it.user_id
                } as ArrayList<String>

                Log.e("uploadedFiles","uploadedFiles: ${uploadedFiles.size}")
                if(uploadedFilesAdapter!=null && uploadedFiles.size>0){
                    uploadedFilesAdapter = null
                    setUploadedFilesAdapter()
                    uploadedFilesAdapter!!.addData(uploadedFiles)
                }
                if(selectedPosition!=null){
                    fragmentEditLockBoxBinding?.documentSpinner?.setSelection(selectedPosition!!)
                }

                if (!usersListFromSelectUser.isNullOrEmpty()) {
                    lockBoxUsersAdapter?.addData(usersListFromSelectUser!!)
                    if (usersList?.size!! > 5) {
                        fragmentEditLockBoxBinding?.txtMore?.visibility = View.VISIBLE
                        val moreUser = usersList?.size!! - 5
                        fragmentEditLockBoxBinding?.txtMore?.text = "+ $moreUser More"
                    } else {
                        fragmentEditLockBoxBinding?.txtMore?.visibility = View.GONE
                    }
                }
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun observeViewModel() {}




    fun observeLifecycle() {
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
                    //hideLoading()
                    dialog?.dismiss()
                    showSuccess(requireContext(), "Files uploaded successfully...")
                    uploadedDocumentsUrl = it.data.payload?.document
                    Log.d(TAG, "Uploaded lockbox docs url: $uploadedDocumentsUrl")
                    fileName = fragmentEditLockBoxBinding?.edtFileName?.text.toString().trim()
                    fileNote =
                        if (fragmentEditLockBoxBinding?.edtNote?.text.toString().isNullOrEmpty()) {
                            null
                        } else {
                            fragmentEditLockBoxBinding?.edtNote?.text.toString().trim()
                        }
                    uploadedDocumentsUrl?.addAll(alreadyAdded)
                    val addNewDocument: ArrayList<Documents> = arrayListOf()
                    for (i in uploadedDocumentsUrl!!) {
                        addNewDocument.add(Documents(i))
                    }
                    addNewDocument.let { list ->
                        deletedSelectedDocs?.let { it1 ->
                            addNewLockBoxViewModel.editNewLockBox(
                                fileName,
                                fileNote,
                                selectedDocumentId?.toInt(), lockBoxId!!,
                                list, it1, usersUUID
                            )
                        }
                    }
                }
            }
        }

        // Observe the response of edit lock box api
        addNewLockBoxViewModel.addNewLockBoxResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    if (isLoading) {
                        showLoading("")
                    }
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), getString(R.string.lockbox_updated_successlly))
                    backPress()
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
                                when {
                                    i.lockbox == null || i.lockbox.size <= 0 -> {
                                        lockBoxTypes.add(i)
                                    }
                                }
                                if (i.name?.lowercase() == "other" && i.lockbox.size > 0 && i.id != lockBoxTypeId) {
                                    lockBoxTypes.add(i)
                                }
                                if (i.lockbox.size > 0 && i.id == lockBoxTypeId) {
                                    lockBoxTypes.add(i)
                                }
                            }
                        }
                    }
                    lockBoxTypes.add(
                        0,
                        LockBoxTypes(id = -1, name = getString(R.string.select_document_type))
                    )
                    if (lockBoxTypes.isEmpty()) return@observeEvent
                    // show types in dropdown
                    documentAdapter =
                        DocumentAdapter(
                            requireContext(),
                            R.layout.vehicle_spinner_drop_view_item,
                            lockBoxTypes
                        )
                    fragmentEditLockBoxBinding?.documentSpinner?.adapter = documentAdapter
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
                    uploadedFiles.clear()
                    fragmentEditLockBoxBinding.let {
                        result.data.payload.let { payload ->
                            it?.edtFileName?.setText(payload?.name)
                            it?.edtNote?.setText(payload?.note)
                            // set document type selection for added document type
                            var position = 0
                            for (i in lockBoxTypes.indices) {
                                if (lockBoxTypes[i].id == result.data.payload?.lbtId) {
                                    position = i
                                }
                            }
                            it?.documentSpinner?.setSelection(position)
//                            if(selectedPosition!=null){
//                                it.documentSpinner.setSelection(selectedPosition!!)
//                            }
                            //set file added adapter

                            if (payload?.documents != null) {
                                if (payload.documents?.size!! > 0) {
                                    for (i in payload.documents!!.indices) {
                                        payload?.createdAt?.let { it1 ->
                                            DocumentData(
                                                i,
                                                payload.documents!![i].url!!,
                                                it1
                                            )
                                        }?.let { it2 ->
                                            uploadedFiles.add(
                                                it2
                                            )
                                        }
                                    }
                                    uploadedFilesAdapter?.addData(uploadedFiles)
                                }
                            } else {
                                setFileViewInvisible()
                            }
                            val allowedUsers = payload?.allowedUsers
                            // Check if select user data is received from Selected User Screen
                            if (!usersListFromSelectUser.isNullOrEmpty()) {
                                usersList = usersListFromSelectUser
                            } else {
                                usersList?.clear()
                                if (!allowedUsers.isNullOrEmpty()) {
                                    allowedUsers.forEach { user ->
                                        val careTeamModel = CareTeamModel(
                                            id = user.id,
                                            user_id = user.uniqueUuid,
                                            user_id_details = LoveUser(
                                                id = user.userProfiles?.id,
                                                firstname = user.userProfiles?.firstname,
                                                lastname = user.userProfiles?.lastname,
                                                profilePhoto = user.userProfiles?.profilePhoto
                                            )
                                        )
                                        usersList?.add(careTeamModel)
                                    }
                                }
                            }

                            usersUUID = usersList?.map {
                                it.user_id
                            } as ArrayList<String>
                            if (!usersList.isNullOrEmpty()) {
                                lockBoxUsersAdapter?.addData(usersList!!)
                                if (usersList?.size!! > 5) {
                                    fragmentEditLockBoxBinding?.txtMore?.visibility = View.VISIBLE
                                    val moreUser = usersList?.size!! - 5
                                    fragmentEditLockBoxBinding?.txtMore?.text = "+ $moreUser More"
                                } else {
                                    fragmentEditLockBoxBinding?.txtMore?.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    // uploaded files from local storage
        private fun handleSelectedFiles(selectedFiles: SingleEvent<ArrayList<File>?>) {
        selectedFiles.getContentIfNotHandled().let {
            if (it != null) {
                dialog?.dismiss()
                val uploadSelectedFiles: ArrayList<DocumentData> = arrayListOf()
                for (i in it) {
                    uploadSelectedFiles.add(
                        DocumentData(
                            -1,
                            i.toString(),
                            dateFormat.format(Calendar.getInstance().time), true
                        )
                    )

                }
                selectedFileList!!.addAll(
                    uploadSelectedFiles
                )
                uploadedFiles.addAll(uploadSelectedFiles)
                if (it.isNotEmpty()) {
                    setFileViewVisible()
                    uploadedFilesAdapter?.addData(uploadSelectedFiles)
                } else {
                    setFileViewInvisible()
                }
            }
        }

    }




    override fun initViewBinding() {}

     fun initBinding() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        addNewLockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
        fragmentEditLockBoxBinding?.listener = this
        lockBoxId = args.id!!.toInt()
        lockBoxTypeId = args.documentId?.toInt()
        addNewLockBoxViewModel.getAllLockBoxTypes(pageNumber, limit, true)
        setUploadedFilesAdapter()
        fragmentEditLockBoxBinding?.documentSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedDocumentId = lockBoxTypes[p2].id.toString()
                    selectedPosition= p2
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        setLockBoxUsersAdapter()



    }

    private fun setLockBoxUsersAdapter() {
        lockBoxUsersAdapter = usersList?.let { LockBoxUsersAdapter(it, addNewLockBoxViewModel) }
        fragmentEditLockBoxBinding?.rvUsers?.adapter = lockBoxUsersAdapter

        /* if (usersList?.size!! > 5) {
             fragmentEditLockBoxBinding.txtMore.visibility = View.VISIBLE
             val moreUser = usersList?.size!! - 5
             fragmentEditLockBoxBinding.txtMore.text = "+ $moreUser More"
         } else {
             fragmentEditLockBoxBinding.txtMore.visibility = View.GONE
         }*/
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
                    fileName = fragmentEditLockBoxBinding?.edtFileName?.text.toString().trim()

                    fileNote =
                        if (fragmentEditLockBoxBinding?.edtNote?.text.toString().isNullOrEmpty()) {
                            null
                        } else {
                            fragmentEditLockBoxBinding?.edtNote?.text.toString().trim()
                        }

                    if (uploadedFiles.isNullOrEmpty()) {
                        if (deletedSelectedDocs.isNullOrEmpty()) {
                            addNewLockBoxViewModel.editNewLockBox(
                                fileName,
                                fileNote,
                                selectedDocumentId?.toInt(),
                                lockBoxId!!,
                                null,
                                null,
                                usersUUID
                            )
                        } else {
                            addNewLockBoxViewModel.editNewLockBox(
                                fileName,
                                fileNote,
                                selectedDocumentId?.toInt(),
                                lockBoxId!!,
                                null,
                                deletedSelectedDocs,
                                usersUUID
                            )
                        }

                    } else {
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
                            Log.e(
                                TAG,
                                "deletedFiles ${deletedSelectedDocs} added : $selectedFileList"
                            )
                            val addedFiles: ArrayList<File> = arrayListOf()
                            alreadyAdded = arrayListOf()
                            for (i in uploadedFiles) {
                                if (i.filePath.startsWith(BuildConfig.BASE_URL)) {
                                    alreadyAdded.add(i.filePath)
                                } else {
                                    addedFiles.add(File(i.filePath))
                                }

                            }
                            if (addedFiles.size > 0) {
                                isLoading = false
                                addedFiles.let { addNewLockBoxViewModel.uploadMultipleLockBoxDoc(it) }
                            } else {
                                isLoading = true
                                //call update api direct
                                fileName =
                                    fragmentEditLockBoxBinding?.edtFileName?.text.toString().trim()
                                fileNote =
                                    if (fragmentEditLockBoxBinding?.edtNote?.text.toString()
                                            .isNullOrEmpty()
                                    ) {
                                        null
                                    } else {
                                        fragmentEditLockBoxBinding?.edtNote?.text.toString().trim()
                                    }

                                uploadedDocumentsUrl?.clear()
                                uploadedDocumentsUrl?.addAll(alreadyAdded)
                                val addNewDocument: ArrayList<Documents> = arrayListOf()
                                for (i in uploadedDocumentsUrl!!) {
                                    addNewDocument.add(Documents(i))
                                }
                                uploadedDocumentsUrl?.let { list ->
                                    deletedSelectedDocs?.let {
                                        addNewLockBoxViewModel.editNewLockBox(
                                            fileName,
                                            fileNote,
                                            selectedDocumentId?.toInt(), lockBoxId!!,
                                            addNewDocument, it, usersUUID
                                        )
                                    }
                                }
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
                if (!usersList.isNullOrEmpty()) {
                    val action =
                        EditLockBoxFragmentDirections.actionNavEditLockboxToSelectUsersFragment(
                            users = usersList?.toTypedArray()
                        )
                    findNavController().navigate(action)
                } else {
                    findNavController().navigate(R.id.action_nav_edit_lockbox_to_selectUsersFragment)
                }
            }
        }
    }

    private fun selectedFileShow() {
        if ((uploadedFiles.size ?: 0) <= 0) {
            setFileViewInvisible()
        } else {
            setFileViewVisible()
        }
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentEditLockBoxBinding?.edtFileName?.text.toString().trim().isEmpty() -> {
                    fragmentEditLockBoxBinding?.edtFileName?.error =
                        getString(R.string.enter_file_name)
                    fragmentEditLockBoxBinding?.edtFileName?.requestFocus()
                }
                selectedDocumentId == null || selectedDocumentId == "-1" -> {
                    showInfo(requireContext(), getString(R.string.please_select_document_type))
                }
                /* fragmentEditLockBoxBinding.edtNote.text.toString().trim().isEmpty() -> {
                     fragmentEditLockBoxBinding.edtNote.error = getString(R.string.enter_note)
                 }*/

                /*uploadedFiles.isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_upload_file))
                }*/
                else -> {
                    return true
                }
            }
            return false
        }



    private fun handleSelectedImage(singleEvent : SingleEvent<File>) {
        singleEvent.getContentIfNotHandled().let {
            if (it != null && it.exists()) {
                addNewLockBoxViewModel.imageFile = it
                addNewLockBoxViewModel.uploadLockBoxDoc(it)
            }

        }
    }


    private fun setUploadedFilesAdapter() {
        uploadedFilesAdapter = UploadedDocumentAdapter()
        fragmentEditLockBoxBinding?.rvUploadedFiles?.adapter = uploadedFilesAdapter
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
                        getString(R.string.unable_to_create_this_request_try_again_later)
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
                                    dateFormat.format(Calendar.getInstance().time),
                                    true
                                )
                            )
                            uploadedFiles.add(
                                DocumentData(
                                    -1,
                                    file.toString(),
                                    dateFormat.format(Calendar.getInstance().time),
                                    true
                                )
                            )
                            if (selectedFileList!!.size > 0) {
                                setFileViewVisible()
                                uploadedFilesAdapter?.addData(selectedFileList!!)
                            } else {
                                setFileViewInvisible()
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
                credential.selectedAccount = googleAccount.account
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
                        val uploadFile: ArrayList<DocumentData> = arrayListOf()
                        for (i in driveSelectedFileList) {
                            uploadFile.add(
                                DocumentData(
                                    -1,
                                    i.toString(),
                                    dateFormat.format(Calendar.getInstance().time)
                                )
                            )
                        }
                        selectedFileList!!.addAll(uploadFile)
                        uploadedFiles.addAll(uploadFile)
                        uploadedFilesAdapter?.addData(uploadFile)
                        selectedFileShow()
                    } else {
                        setFileViewInvisible()
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
                        setFileViewVisible()
                        val uploadFile: ArrayList<DocumentData> = arrayListOf()
                        uploadFile.add(
                            DocumentData(
                                -1,
                                file.toString(),
                                dateFormat.format(Calendar.getInstance().time)
                            )
                        )
                        selectedFileList?.addAll(uploadFile)
                        uploadedFiles.addAll(uploadFile)
                        uploadedFilesAdapter?.addData(uploadFile)

                        selectedFileShow()
                        hideLoading()
                    }
                }
            }
        }

    private fun setFileViewInvisible() {
        fragmentEditLockBoxBinding?.txtUploadedFiles?.visibility = View.GONE
        fragmentEditLockBoxBinding?.rvUploadedFiles?.visibility = View.GONE
        fragmentEditLockBoxBinding?.txtNoUploadedLockBoxFile?.visibility =
            View.GONE
    }

    private fun setFileViewVisible() {
        fragmentEditLockBoxBinding?.txtUploadedFiles?.visibility = View.VISIBLE
        fragmentEditLockBoxBinding?.rvUploadedFiles?.visibility =
            View.VISIBLE
        fragmentEditLockBoxBinding?.txtNoUploadedLockBoxFile?.visibility =
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

    override fun onItemClick(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val dialog = builder.apply {
            setTitle(getString(R.string.delete_upload_document))
            setMessage(getString(R.string.are_you_sure_u_want_to_delete_uploaded_document))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!uploadedFiles[position].newAdded) {
                    deletedSelectedDocs?.add(Documents(uploadedFiles[position].filePath))
                }
                uploadedFiles.removeAt(position)
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

    override fun onResume() {
        super.onResume()
        // Get data back from the launched fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinded = false
    }
}