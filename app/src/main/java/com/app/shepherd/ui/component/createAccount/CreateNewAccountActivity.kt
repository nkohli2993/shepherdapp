package com.app.shepherd.ui.component.createAccount

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import com.app.shepherd.BuildConfig
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityCreateNewAccountBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.ui.welcome.WelcomeActivity
import com.app.shepherd.ui.component.welcome.WelcomeUserActivity
import com.app.shepherd.utils.*
import com.app.shepherd.utils.extensions.isValidEmail
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showInfo
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.view_model.CreateNewAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_create_account.editTextPhoneNumber
import kotlinx.android.synthetic.main.activity_create_new_account.*
import java.io.File


/**
 * Created by Deepak Rattan on 31-05-22
 */
@AndroidEntryPoint
class CreateNewAccountActivity : BaseActivity(), View.OnClickListener {

    private val createNewAccountViewModel: CreateNewAccountViewModel by viewModels()
    private lateinit var binding: ActivityCreateNewAccountBinding
    private var phoneCode: String? = null
    private var profilePicUrl: String? = null
    private var email: String? = null
    private var passwd: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var phoneNumber: String? = null
    private var roleId: String? = null
    private var isPasswordShown = false
    private var TAG = "CreateNewAccountActivity"


    // Handle Validation
    private val isValid: Boolean
        get() {
            when {
                binding.edtFirstName.text.toString().isEmpty() -> {
                    binding.edtFirstName.error = getString(R.string.please_enter_first_name)
                    binding.edtFirstName.requestFocus()
                }
                binding.edtLastName.text.toString().isEmpty() -> {
                    binding.edtLastName.error = getString(R.string.please_enter_last_name)
                    binding.edtLastName.requestFocus()
                }
                binding.editTextEmail.text.toString().isEmpty() -> {
                    binding.editTextEmail.error = getString(R.string.please_enter_email_id)
                    binding.editTextEmail.requestFocus()
                }
                !binding.editTextEmail.text.toString().isValidEmail() -> {
                    binding.editTextEmail.error = getString(R.string.please_enter_valid_email_id)
                    binding.editTextEmail.requestFocus()
                }
                binding.edtPhoneNumber.text.toString().isEmpty() -> {
                    binding.edtPhoneNumber.error = getString(R.string.enter_phone_number)
                    binding.edtPhoneNumber.requestFocus()
                }
                binding.editTextPassword.text.toString().isEmpty() -> {
                    binding.editTextPassword.error = getString(R.string.please_enter_your_password)
                    binding.editTextPassword.requestFocus()
                }
                !binding.checkboxTermsConditions.isChecked -> {
                    showInfo(
                        this,
                        "Please select the box describing Terms & Condition and Privacy Policy."
                    )
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
        //setPhoneNumberFormat()

        binding.ccp.setOnCountryChangeListener { this.phoneCode = it.phoneCode }

        // Handle the click of Show or Hide Password Icon
        binding.imageViewPasswordToggle.setOnClickListener {
            if (isPasswordShown) {
                //Hide Password
                binding.editTextPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                //Show password
                binding.editTextPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            }
            isPasswordShown = !isPasswordShown
            binding.editTextPassword.setSelection(binding.editTextPassword.length())
        }
    }

    override fun initViewBinding() {
        binding = ActivityCreateNewAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        // Observe the response of upload image api
        createNewAccountViewModel.uploadImageLiveData.observeEvent(this) {
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
                        it1.message?.let { it2 -> showSuccess(this, it2) }
                        profilePicUrl = it1.payload?.profilePhoto
                        Log.d(TAG, "ProfilePicURL:$profilePicUrl ")
                    }
                }
            }
        }

        // Observe the response of sign up api
        createNewAccountViewModel.signUpLiveData.observeEvent(this) {
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
                        it1.message?.let { it2 -> showSuccess(this, it2) }

                        // Save User's Info to Shared Preferences
                        // it1.payload?.let { it2 -> createNewAccountViewModel.saveUser(it2) }
                        // navigateToHomeScreen()
                        // navigateToLoginScreen()


                        // Save Token to SharedPref
                        it1.payload?.let { payload ->
                            payload.token?.let { token ->
                                createNewAccountViewModel.saveToken(
                                    token
                                )
                            }

                            payload.id?.let { userId ->
                                createNewAccountViewModel.saveUserId(
                                    userId
                                )
                            }
                        }
                        if (BiometricUtils.isSdkVersionSupported && BiometricUtils.isHardwareSupported(
                                this
                            ) && BiometricUtils.isFingerprintAvailable(
                                this
                            )
                        ) {
                            showBioMetricDialog()
                        } else {
                            navigateToWelcomeUserScreen()
                        }
                    }

                }
            }
        }

        createNewAccountViewModel.bioMetricLiveData.observeEvent(this) {
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
                        navigateToWelcomeUserScreen()
                    }
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        startActivityWithFinish<HomeActivity>()
    }

    private fun navigateToLoginScreen() {
        startActivityWithFinish<LoginActivity>()
    }


    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            //imgUploadProfilePic.visibility = View.GONE

            createNewAccountViewModel.imageFile = file
            createNewAccountViewModel.uploadImage(file)
            imgProfile.loadImageCentreCrop(R.drawable.ic_outline_person, file)
            imgProfile.scaleType = ImageView.ScaleType.FIT_XY
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            // Back icon of Toolbar
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.txtLogin -> {
                navigateToLoginScreen()
            }
            // Upload Profile Pic
            R.id.imgUploadProfilePic -> {
                openImagePicker()
            }
            // Create Account
            R.id.btnCreate -> {
                if (isValid) {
                    firstName = edtFirstName.text.toString().trim()
                    lastName = edtLastName.text.toString().trim()
                    email = editTextEmail.text.toString().trim()
                    passwd = editTextPassword.text.toString().trim()
                    phoneNumber = edtPhoneNumber.text.toString().trim()
                    phoneCode = ccp.selectedCountryCode
                    createNewAccountViewModel.createAccount(
                        phoneCode,
                        BuildConfig.BASE_URL + profilePicUrl,
                        firstName,
                        lastName,
                        email,
                        passwd,
                        phoneNumber
                    )

                }
                /*else {
                        showInfo(
                            this,
                            "Please select the box describing Terms & Condition and Privacy Policy."
                        )
                    }*/
            }
        }
    }

    private fun showBioMetricDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bio_metric)
        val yesBtn = dialog.findViewById(R.id.btnYes) as Button
        val noBtn = dialog.findViewById(R.id.btnNo) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            registerBiometric(true)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            navigateToWelcomeUserScreen()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun registerBiometric(isBioMetricEnable: Boolean) {
        createNewAccountViewModel.registerBioMetric(
            isBioMetricEnable
        )
    }

    private fun setPhoneNumberFormat() {
        editTextPhoneNumber.addTextChangedListener(
            PhoneTextFormatter(
                editTextPhoneNumber, getString(
                    R.string.us_phone_pattern
                )
            )
        )

    }


    private fun navigateToWelcomeScreen() {
        startActivity<WelcomeActivity>()
    }

    private fun navigateToWelcomeUserScreen() {
        Prefs.with(this)!!.save(Const.SECOND_TIME_LOGIN, true)
        startActivity<WelcomeUserActivity>()
    }

}

