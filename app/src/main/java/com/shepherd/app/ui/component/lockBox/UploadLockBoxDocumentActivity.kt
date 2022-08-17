package com.shepherd.app.ui.component.lockBox

import CommonFunctions
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.AddNewLockBoxViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.shepherd.app.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class UploadLockBoxDocumentActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var uploadedLockBoxDocUrl: String? = null
    private val addNewLockBoxViewModel: AddNewLockBoxViewModel by viewModels()
    override fun observeViewModel() {
        addNewLockBoxViewModel.uploadLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.message?.let { it1 -> showSuccess(this, it1) }
                    uploadedLockBoxDocUrl = it.data.payload.document
                    finish()
                    Log.d(TAG, "uploadedLockBoxDocUrl: $uploadedLockBoxDocUrl")
                }
            }
        }

    }

    override fun initViewBinding() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_lock_box_document)
        requestSignIn()

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                if (resultCode == RESULT_OK && resultData != null) {
                    handleSignInResult(resultData)
                } else {
                    showError(this, "Unable to create this request try, again later!")
                    finish()
                    Log.e("catch_exception", "Request not accepted $resultCode")
                }
            }
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == RESULT_OK && resultData != null) {
                val uri = resultData.data
                uri?.let {
                    val file = File(uri.toString())
                    if (file != null && file.exists()) {
                        addNewLockBoxViewModel.imageFile = file

                        // TODO: call api to handle to get data back to added screen
                        addNewLockBoxViewModel.uploadLockBoxDoc(file)
                    }
                    //                    openFileFromFilePicker(it)
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            client.signOut()
        }

        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                Log.d(
                    TAG,
                    "Signed in as " + googleAccount.email
                )
                val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                    this, setOf(DriveScopes.DRIVE_FILE)
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
            Log.d(TAG, "Opening file picker.")
            val pickerIntent: Intent = mDriveServiceHelper!!.createFilePickerIntent()
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT )
//            filePick.launch(pickerIntent)
        }
    }

    private val filePick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (!selectedMedia.isNullOrEmpty()) {
                    var file: File = if (selectedMedia[0].path?.startsWith("content")!!) {
                        CommonFunctions.fileFromContentUri(
                            this,
                            selectedMedia[0].path.toString().toUri()
                        )
                    } else {
                        File(selectedMedia[0].path!!)
                    }
                    selectedFile.value = file

                    if (file.exists()) {
                        addNewLockBoxViewModel.imageFile = file
                        addNewLockBoxViewModel.uploadLockBoxDoc(file)
                    }

                }
            }
        }


    companion object {
        private const val TAG = "UploadDrive"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}