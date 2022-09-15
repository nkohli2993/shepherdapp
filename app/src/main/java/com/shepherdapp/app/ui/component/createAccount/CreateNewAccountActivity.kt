package com.shepherdapp.app.ui.component.createAccount

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.ActivityCreateNewAccountBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.ui.component.welcome.WelcomeUserActivity
import com.shepherdapp.app.ui.welcome.WelcomeActivity
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.isValidEmail
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.CreateNewAccountViewModel
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
    private var profilePicCompleteUrl: String? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var TAG = "CreateNewAccountActivity"
    private val PERMISSION_REQUEST_CODE = 200
    private lateinit var navController: NavController

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
        // Get Roles
        createNewAccountViewModel.getRoles(pageNumber, limit)
    }

    override fun initViewBinding() {
        binding = ActivityCreateNewAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClicks(binding.checkboxText.text.toString())
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

                            payload.uuid?.let { uuid ->
                                createNewAccountViewModel.saveUUID(
                                    uuid
                                )
                            }
                            payload.email?.let { email ->
                                createNewAccountViewModel.saveEmail(
                                    email
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
                            Prefs.with(this)!!
                                .save(Const.BIOMETRIC_ENABLE, payload.userProfiles?.isBiometric!!)
                        }
                        navigateToWelcomeUserScreen()
                    }
                }
            }
        }

        // Observe the response of Roles api
        createNewAccountViewModel.rolesResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    it.message?.let { showError(this, it.toString()) }
                }
                is DataResult.Loading -> {

                }
                is DataResult.Success -> {
                    roleId = it.data.payload.users.filter { users ->
                        users.slug == UserSlug.User.slug
                    }.map { user ->
                        user.id
                    }[0].toString()
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
            R.id.imgUploadProfilePic, R.id.imgProfile -> {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    openImagePicker()
                }

            }
            // Create Account
            R.id.btnCreate -> {
                if (isValid) {
                    createNewAccountViewModel.saveEmail(
                        editTextEmail.text.toString().trim()
                    )
                    firstName = edtFirstName.text.toString().trim()
                    lastName = edtLastName.text.toString().trim()
                    email = editTextEmail.text.toString().trim()
                    passwd = editTextPassword.text.toString().trim()
                    phoneNumber = edtPhoneNumber.text.toString().trim()
                    phoneCode = ccp.selectedCountryCode
                    profilePicCompleteUrl = if (profilePicUrl.isNullOrEmpty()) {
                        null
                    } else {
                        BuildConfig.BASE_URL_USER + profilePicUrl
//                        ApiConstants.BASE_URL_USER + profilePicUrl
                    }

                    createNewAccountViewModel.createAccount(
                        phoneCode,
                        profilePicCompleteUrl,
                        firstName,
                        lastName,
                        email,
                        passwd,
                        phoneNumber,
                        roleId
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
        val yesBtn = dialog.findViewById(R.id.btnYes) as TextView
        val noBtn = dialog.findViewById(R.id.btnNo) as TextView
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
        startActivityWithFinish<WelcomeUserActivity>()
    }

    private fun setClicks(text: String) {
        val ss = SpannableString(text)
        val termsConditionClick: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                val intent = Intent(this@CreateNewAccountActivity, InformationActivity::class.java)
                intent.putExtra("type", Const.TERM_OF_USE)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(applicationContext, R.color._A26DCB)
                ds.linkColor = ContextCompat.getColor(applicationContext, R.color._A26DCB)
            }
        }
        val privacyPolicyClick: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                val intent = Intent(this@CreateNewAccountActivity, InformationActivity::class.java)
                intent.putExtra("type", Const.PRIVACY_POLICY)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(applicationContext, R.color._A26DCB)
                ds.linkColor = ContextCompat.getColor(applicationContext, R.color._A26DCB)
            }
        }
        ss.setSpan(termsConditionClick, 15, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(privacyPolicyClick, 37, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.checkboxText.text = ss
        binding.checkboxText.movementMethod = LinkMovementMethod.getInstance()
    }

}
