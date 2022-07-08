package com.app.shepherd.ui.component.login

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
import android.widget.TextView
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.dto.login.UserLovedOne
import com.app.shepherd.databinding.ActivityLoginNewBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.createAccount.CreateNewAccountActivity
import com.app.shepherd.ui.component.forgot_password.ForgotPasswordActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.ui.component.resetPassword.ResetPasswordActivity
import com.app.shepherd.ui.component.welcome.WelcomeUserActivity
import com.app.shepherd.utils.*
import com.app.shepherd.utils.Const.BIOMETRIC_ENABLE
import com.app.shepherd.utils.Const.SECOND_TIME_LOGIN
import com.app.shepherd.utils.extensions.isValidEmail
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.view_model.LoginViewModel
import com.google.android.material.snackbar.Snackbar
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
                        // Save User Detail to SharedPref
                        it.payload?.userProfile?.let { it1 -> loginViewModel.saveUser(it1) }

                        // Save user ID
                        it.payload?.userProfile?.let { it1 ->
                            it1.userId?.let { it2 ->
                                loginViewModel.saveUserId(
                                    it2
                                )
                            }
                        }

                        // Save UUID
                        it.payload?.uuid.let { uuid ->
                            uuid?.let { it1 -> loginViewModel.saveUUID(it1) }
                        }

                        // Save token
                        it.payload?.token?.let { it1 -> loginViewModel.saveToken(it1) }
                        token = it.payload?.token
                        it.payload?.userLovedOne?.let {
                            if (it.isNotEmpty()) {
                                loginViewModel.saveLovedOneId(it[0].loveUserId)
                            }
                        }

                        userLovedOneArrayList = it.payload?.userLovedOne

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
                        if (userLovedOneArrayList.isNullOrEmpty()) {
                            navigateToWelcomeUserScreen()
                        } else {
                            //navigateToHomeScreen()
                            navigateToHomeScreenWithLovedOneArray()
                        }
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
                            Prefs.with(this)!!.save(Const.BIOMETRIC_ENABLE, payload.isBiometric!!)
                        }
                        navigateToHomeScreen()
                    }
                }
            }
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
            if (userLovedOneArrayList.isNullOrEmpty()) {
                navigateToAddLovedOneScreen()
            } else {
                navigateToHomeScreen()
            }
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun registerBiometric(isBioMetricEnable: Boolean) {
        loginViewModel.registerBioMetric(
            isBioMetricEnable
        )
    }

    private fun navigateToHomeScreen() {
        Prefs.with(this)!!.save(SECOND_TIME_LOGIN, true)
        startActivityWithFinish<HomeActivity>()
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
                    it.device = CommonFunctions.getDeviceId(this@LoginActivity)
                }
            } else {
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
        intent.putExtra(Const.LOVED_ONE_ARRAY, userLovedOneArrayList)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
