package com.app.shepherd.ui.component.createAccount

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
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
import com.app.shepherd.ui.component.welcome.WelcomeActivity
import com.app.shepherd.utils.PhoneTextFormatter
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showInfo
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.utils.loadImageCentreCrop
import com.app.shepherd.utils.observe
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
                    it.errorCode?.let { showError(this, it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.let { it1 ->
                        it1.message?.let { it2 -> showSuccess(this, it2) }
                        profilePicUrl = it1.payload?.profilePhoto
                    }
                }
            }
        }

        // Observe the response of sign up api
        createNewAccountViewModel.signUpLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.errorCode?.let { showError(this, it.toString()) }

                }
                is DataResult.Loading -> {
                    showLoading("")

                }
                is DataResult.Success -> {
                    it.data.let { it1 ->
                        it1.message?.let { it2 -> showSuccess(this, it2) }

                        // Save User's Info to Shared Preferences
                        // it1.payload?.let { it2 -> createNewAccountViewModel.saveUser(it2) }
                        // navigateToHomeScreen()
                        navigateToLoginScreen()
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
            // Upload Profile Pic
            R.id.imgUploadProfilePic -> {
                openImagePicker()
            }
            // Create Account
            R.id.btnCreate -> {
                if (binding.checkboxTermsConditions.isChecked) {
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
                } else {
                    showInfo(
                        this,
                        "Please select the box describing Terms & Condition and Privacy Policy."
                    )
                }
            }
        }
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

}

