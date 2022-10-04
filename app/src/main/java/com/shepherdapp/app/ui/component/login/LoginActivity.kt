package com.shepherdapp.app.ui.component.login

import CommonFunctions
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.chat.User
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.databinding.ActivityLoginNewBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.createAccount.CreateNewAccountActivity
import com.shepherdapp.app.ui.component.forgot_password.ForgotPasswordActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.joinCareTeam.JoinCareTeamActivity
import com.shepherdapp.app.ui.component.resetPassword.ResetPasswordActivity
import com.shepherdapp.app.ui.component.welcome.WelcomeUserActivity
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.Const.BIOMETRIC_ENABLE
import com.shepherdapp.app.utils.Const.SECOND_TIME_LOGIN
import com.shepherdapp.app.utils.extensions.isValidEmail
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginNewBinding
    private var isPasswordShown = false
    private var isBioMetricLogin = false
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private var token: String? = null
    private var userLovedOneArrayList: ArrayList<UserLovedOne>? = null
    private var firebaseToken: String? = null
    private var TAG = "LoginActivity"

    // Handle Validation
    private val isValid: Boolean
        get() {
            when {
                loginViewModel.loginData.value?.email.isNullOrEmpty() -> {
                    binding.edtEmail.error = getString(R.string.please_enter_email_id)
                    binding.edtEmail.requestFocus()
                }
                loginViewModel.loginData.value?.email?.isValidEmail() == false -> {
                    binding.edtEmail.error = getString(R.string.please_enter_valid_email_id)
                    binding.edtEmail.requestFocus()
                }
                loginViewModel.loginData.value?.password.isNullOrEmpty() -> {
                    binding.edtPasswd.error = getString(R.string.please_enter_your_password)
                    binding.edtPasswd.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this

        loginViewModel.loginData.value!!.email = "karan@yopmail.com"
        loginViewModel.loginData.value!!.password = "Admin@123"

//        loginViewModel.loginData.value!!.email = "pooja@yopmail.com"
//        loginViewModel.loginData.value!!.password = "Welcome@123"

        binding.viewModel = loginViewModel


        // Handle the click of Show or Hide Password Icon
        binding.imageViewPasswordToggle.setOnClickListener {
            if (isPasswordShown) {
                //Hide Password
                binding.edtPasswd.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                //Show password
                binding.edtPasswd.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            }
            isPasswordShown = !isPasswordShown
            binding.edtPasswd.setSelection(binding.edtPasswd.length())
        }
        fingerPrintExecute()

    }

    private fun fingerPrintExecute() {
        if (BiometricUtils.isSdkVersionSupported && BiometricUtils.isHardwareSupported(this) && BiometricUtils.isFingerprintAvailable(
                this
            ) && Prefs.with(this)!!.getBoolean(BIOMETRIC_ENABLE)
        ) {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        when (errorCode) {
                            BiometricPrompt.ERROR_CANCELED,
                            BiometricPrompt.ERROR_USER_CANCELED -> {
                            }
                        }
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        doLogin(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                })
            val promptInfo = if (Build.VERSION.SDK_INT < 30) {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.prompt_info_title))
                    .setSubtitle(getString(R.string.prompt_info_subtitle))
//                .setNegativeButtonText("Cancel")
                    .setDeviceCredentialAllowed(true)
                    .setConfirmationRequired(false)
                    .build()
            } else {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.prompt_info_title))
                    .setSubtitle(getString(R.string.prompt_info_subtitle))
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                    .setConfirmationRequired(false)
                    .build()
            }

            biometricPrompt.authenticate(promptInfo)
        } else {
            binding.apply {
                llBiometricWrapper.isVisible = false
                imgBiometric.isVisible = false
            }
        }
    }

    override fun initViewBinding() {
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun observeViewModel() {
        // Observe Login response
        loginViewModel.loginResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
//                    it.data.message?.let { it1 -> showSuccess(this, it1) }
                    it.data.let { it ->
                        it.message?.let { it1 -> showSuccess(this, it1) }
                        it.payload.let { payload ->
                            payload?.userProfiles?.let { it1 ->
                                // Save UserProfiles
                                loginViewModel.saveUser(it1)

                                // Save UserID
                                it1.userId?.let { userID ->
                                    loginViewModel.saveUserId(userID)

                                }
                            }
//                            generateFirebaseToken()
                            // Save UUID
                            payload?.uuid.let { uuid ->
                                uuid?.let { it1 -> loginViewModel.saveUUID(it1) }
                            }

                            // Save token
                            payload?.token?.let { it1 -> loginViewModel.saveToken(it1) }

                            payload?.userLovedOne?.let {
                                if (it.isNotEmpty()) {
                                    // Save Loved One UUID
                                    it[0].let {
                                        loginViewModel.saveLovedOneDetail(it)
                                    }
                                    it[0].loveUserId?.let { it1 ->
                                        loginViewModel.saveLovedOneUUID(
                                            it1
                                        )
                                    }
                                    // Save LovedOne ID
                                    it[0].id?.let { it1 ->
                                        loginViewModel.saveLovedOneId(
                                            it1.toString()
                                        )
                                    }

                                    // Save Role
                                    it[0].careRoles?.name.let {
                                        it?.let { it1 -> loginViewModel.saveUserRole(it1) }
                                    }
                                }
                            }
                            payload?.email?.let { email ->
                                loginViewModel.saveEmail(
                                    email
                                )
                            }
                        }
                        userLovedOneArrayList = it.payload?.userLovedOne

                        // Save User Info in Firestore
                        val user = it.payload?.let { it1 -> loginResponseToUser(it1) }
                        user?.let { it1 -> loginViewModel.saveUserInfoInFirestore(it1) }

                        // val lovedOneUserID = it.payload?.userLovedOne?.get(0)?.loveUserId
                        // Save lovedOneID to sharedPref
                        // lovedOneUserID?.let { it1 -> loginViewModel.saveLovedOneId(it1) }
                    }
                    if (BiometricUtils.isSdkVersionSupported && BiometricUtils.isHardwareSupported(
                            this
                        ) && BiometricUtils.isFingerprintAvailable(
                            this
                        ) && !isBioMetricLogin && !Prefs.with(this)!!.getBoolean(SECOND_TIME_LOGIN)

                    ) {
                        showBioMetricDialog()
                    } else {
                        navigateToScreen()

                    }
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }

                }
            }

        }

        // Observe biometric response
        loginViewModel.bioMetricLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }

                }
                is DataResult.Loading -> {
                    showLoading("")

                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.let { it1 ->
                        // Save Token to SharedPref
                        it1.payload?.let { payload ->
                            Prefs.with(this)!!
                                .save(Const.BIOMETRIC_ENABLE, payload.userProfiles?.isBiometric!!)
                        }
                        Prefs.with(this)!!.save(SECOND_TIME_LOGIN, true)
                        navigateToScreen()
                    }
                }
            }
        }
    }

    private fun navigateToScreen() {

        if (userLovedOneArrayList.isNullOrEmpty()) {
            navigateToWelcomeUserScreen()
        } else {
            //navigateToHomeScreen()
            navigateToHomeScreenWithLovedOneArray()
        }
    }

    private fun showBioMetricDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bio_metric)
        val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
        val noBtn = dialog.findViewById(R.id.btnNo) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            registerBiometric(true)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            navigateToScreen()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    // Show Enterprise user dialog
    private fun showEnterpriseUserDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enterprise_user)
        val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
        val noBtn = dialog.findViewById(R.id.btnNo) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    // Enter Enterprise code dialog
    private fun showEnterpriseCodeDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enterprise_code)
        val edtEnterCode = dialog.findViewById(R.id.edtEnterCode) as EditText
        val btnSubmit = dialog.findViewById(R.id.btnSubmit) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnSubmit.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun registerBiometric(isBioMetricEnable: Boolean) {
        loginViewModel.registerBioMetric(
            isBioMetricEnable
        )
    }


    private fun navigateToAddLovedOneScreen() {
        startActivityWithFinish<AddLovedOneActivity>()
    }

    private fun navigateToWelcomeUserScreen() {
        startActivityWithFinish<WelcomeUserActivity>()
    }

    private fun navigateToJoinCareScreen() {
        startActivity<JoinCareTeamActivity>()
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtForgotPassword -> {
                navigateToForgotPasswordScreen()
            }
            R.id.btnLogin -> {
                if (isValid) {
                    doLogin(false)
                }
            }
            R.id.txtCreateAccount -> {
                navigateToCreateNewAccountScreen()
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.imgBiometric -> {
                fingerPrintExecute()
            }
        }
    }

    private fun navigateToCreateNewAccountScreen() {
        startActivity<CreateNewAccountActivity>()
    }

    private fun navigateToForgotPasswordScreen() {
        startActivity<ForgotPasswordActivity>()
    }

    private fun doLogin(isBioMetric: Boolean) {
        isBioMetricLogin = isBioMetric

        loginViewModel.apply {
            if (isBioMetric) {
                loginData.value?.let {
                    it.device = CommonFunctions.getDeviceId(this@LoginActivity) + "${
                        Prefs.with(
                            ShepherdApp.appContext
                        )!!.getString(Const.EMAIL_ID, "")
                    }"
                }
            } else {
                loginViewModel.saveEmail(
                    binding.edtEmail.text.toString().trim()
                )

                loginData.value?.let {
                    it.device = null
                }
            }
            login(isBioMetric)
        }
    }

    private fun navigateToResetPasswordScreen() {
        startActivity<ResetPasswordActivity>()
    }

    // Navigate to Home Screen with loved one array
    private fun navigateToHomeScreenWithLovedOneArray() {
        if (!userLovedOneArrayList.isNullOrEmpty()) {
            Log.d(TAG, "LovedOneArrayList Size :${userLovedOneArrayList?.size} ")
        } else {
            Log.d(TAG, "LovedOneArrayList is null")
        }

        val intent = Intent(this, HomeActivity::class.java)
//        intent.putExtra(Const.LOVED_ONE_ARRAY, userLovedOneArrayList)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun generateFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }
            firebaseToken = it.result
            // Get new FCM registration token
            Prefs.with(this)!!.save(Const.FIREBASE_TOKEN, it.result)
            // Log and toast
            Log.d(TAG, "Firebase token generated: ${it.result}")
        }
    }


    private fun loginResponseToUser(payload: Payload): User {
        val fToken = Prefs.with(this)?.getString(Const.FIREBASE_TOKEN)

        return User().apply {
            id = payload.userProfiles?.id
            userId = payload.userProfiles?.userId
            firstname = payload.userProfiles?.firstname
            lastname = payload.userProfiles?.lastname
            profilePhoto = payload.userProfiles?.profilePhoto
            uuid = payload.uuid
            email = payload.email
            firebaseToken = fToken
        }
    }

}
